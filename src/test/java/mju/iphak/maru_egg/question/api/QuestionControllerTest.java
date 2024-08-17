package mju.iphak.maru_egg.question.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import mju.iphak.maru_egg.answer.application.AnswerService;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.domain.AnswerReference;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerReferenceRepository;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.application.QuestionProcessingService;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.FindQuestionsRequest;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.request.SearchQuestionsRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;
import reactor.core.publisher.Mono;

class QuestionControllerTest extends IntegrationTest {

	@Autowired
	private QuestionProcessingService questionProcessingService;

	@Autowired
	private QuestionService questionService;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private AnswerReferenceRepository answerReferenceRepository;

	@Autowired
	private AnswerService answerService;

	private Question question;
	private Answer answer;
	private AnswerReference answerReference;
	private QuestionType type;
	private QuestionCategory category;
	private String content;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Mock
	private ExchangeFunction exchangeFunction;

	@Value("${web-client.base-url}")
	private String llmBaseUrl;

	@BeforeEach
	void setUp() {
		String checkIndexQuery = "SHOW INDEX FROM questions WHERE Key_name = 'idx_ft_question_content'";
		List<?> result = jdbcTemplate.queryForList(checkIndexQuery);
		if (!result.isEmpty()) {
			jdbcTemplate.execute("ALTER TABLE questions DROP INDEX idx_ft_question_content");
		}
		jdbcTemplate.execute("ALTER TABLE questions ADD FULLTEXT INDEX idx_ft_question_content(content)");
		type = QuestionType.SUSI;
		category = QuestionCategory.ADMISSION_GUIDELINE;
		content = "수시 원서 일정 알려주세요.";
		question = questionRepository.save(Question.of("수시 원서 일정 알려주세요.", "수시 일정", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE));
		answer = answerRepository.save(Answer.of(question,
			"수시 일정은 2024년 12월 19일(목)부터 2024년 12월 26일(목) 18:00까지 최초합격자 발표가 있고, 2025년 2월 10일(월) 10:00부터 2025년 2월 12일(수) 15:00까지 문서등록 및 등록금 납부가 진행됩니다. 등록금 납부 기간은 2024년 12월 16일(월) 10:00부터 2024년 12월 18일(수) 15:00까지이며, 방법은 입학처 홈페이지를 통한 문서등록 및 등록금 납부를 하시면 됩니다. 상세 안내는 추후 입학처 홈페이지를 통해 공지될 예정입니다."));
		answerReference = answerReferenceRepository.save(AnswerReference.of("테스트 title", "테스트 link", answer));

		WebClient webClient = WebClient.builder()
			.exchangeFunction(exchangeFunction)
			.baseUrl(llmBaseUrl)
			.build();

		answerService = new AnswerService(answerRepository, webClient);

		ClientResponse clientResponse = ClientResponse.create(HttpStatusCode.valueOf(200))
			.header("Content-Type", "application/json")
			.body("{\"answer\":\"" + answer.getContent() + "\"}")
			.build();

		when(exchangeFunction.exchange(any())).thenReturn(Mono.just(clientResponse));
	}

	@Test
	void 질문_API() throws Exception {
		// given
		QuestionRequest request = new QuestionRequest(type, category, content);
		AnswerResponse answerResponse = AnswerResponse.from(answer);

		// when
		ResultActions resultActions = requestCreateQuestion(request);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(question.getContent()))
			.andExpect(jsonPath("$.answer.content").isNotEmpty())
			.andExpect(jsonPath("$.answer.renewalYear").value(answerResponse.renewalYear()));
	}

	@Test
	void 질문_목록_조회_API() throws Exception {
		// given
		AnswerResponse answerResponse = AnswerResponse.from(answer);
		FindQuestionsRequest request = new FindQuestionsRequest(type, category);

		System.out.println(questionRepository.findAll().get(0).getQuestionType());
		// when
		ResultActions resultActions = requestFindQuestions(request);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].content").value(content))
			.andExpect(jsonPath("$[0].answer.content").value(answerResponse.content()))
			.andExpect(jsonPath("$[0].answer.renewalYear").value(answerResponse.renewalYear()));
	}

	@Test
	void 질문_목록_조회_API_카테고리_없이() throws Exception {
		// given
		AnswerResponse answerResponse = AnswerResponse.from(answer);

		FindQuestionsRequest request = new FindQuestionsRequest(type, null);
		List<AnswerReferenceResponse> references = new ArrayList<>(
			List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link")));
		QuestionResponse response = QuestionResponse.of(question, answerResponse, references);

		// when
		ResultActions resultActions = requestFindQuestionsWithoutCategory(request);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].content").value(content))
			.andExpect(jsonPath("$[0].answer.content").value(answerResponse.content()))
			.andExpect(jsonPath("$[0].answer.renewalYear").value(answerResponse.renewalYear()));
	}

	@Test
	void 질문_자동완성_API() throws Exception {
		// given
		SearchQuestionsRequest request = new SearchQuestionsRequest("example content", 5, 0, 0L);
		SearchedQuestionsResponse searchedQuestionsResponse = new SearchedQuestionsResponse(1L, "example content");
		SliceQuestionResponse<SearchedQuestionsResponse> response = new SliceQuestionResponse<>(
			List.of(searchedQuestionsResponse), 0, 5, false, null, null);

		// when
		ResultActions resultActions = requestSearchQuestions(request);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray());
	}

	@Test
	void 질문_생성_API() throws Exception {
		// given
		CreateAnswerRequest answerRequest = new CreateAnswerRequest("example answer content", 2024);
		CreateQuestionRequest request = new CreateQuestionRequest("example content", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE, answerRequest);

		// when
		ResultActions resultActions = requestCreateQuestion(request);

		// then
		resultActions
			.andExpect(status().isOk());
	}

	private ResultActions requestCreateQuestion(QuestionRequest dto) throws Exception {
		return mvc.perform(post("/api/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andDo(print());
	}

	private ResultActions requestFindQuestions(FindQuestionsRequest dto) throws Exception {
		return mvc.perform(get("/api/questions")
				.param("type", dto.type().name())
				.param("category", dto.category().name())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private ResultActions requestFindQuestionsWithoutCategory(FindQuestionsRequest dto) throws Exception {
		return mvc.perform(get("/api/questions")
				.param("type", dto.type().name())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private ResultActions requestSearchQuestions(SearchQuestionsRequest dto) throws Exception {
		return mvc.perform(get("/api/questions/search")
				.param("content", dto.content())
				.param("size", dto.size().toString())
				.param("cursorViewCount", dto.cursorViewCount().toString())
				.param("questionId", dto.questionId().toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private ResultActions requestCreateQuestion(CreateQuestionRequest dto) throws Exception {
		return mvc.perform(post("/api/questions/new")
				.content(objectMapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}
}