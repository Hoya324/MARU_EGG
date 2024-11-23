package mju.iphak.maru_egg.question.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.answerreference.domain.AnswerReference;
import mju.iphak.maru_egg.answerreference.repository.AnswerReferenceRepository;
import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.question.application.query.find.FindAllQuestionsService;
import mju.iphak.maru_egg.question.application.query.process.ProcessQuestionService;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.request.FindQuestionsRequest;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.request.SearchQuestionsRequest;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

class QuestionControllerTest extends IntegrationTest {

	@Autowired
	private ProcessQuestionService processQuestion;

	@Autowired
	private FindAllQuestionsService findAllQuestionsService;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private AnswerReferenceRepository answerReferenceRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Mock
	private ExchangeFunction exchangeFunction;

	@Value("${web-client.base-url}")
	private String llmBaseUrl;

	private Question question;
	private Answer answer;
	private String content;

	@BeforeEach
	void setUp() {
		setupDatabase();
		initializeTestData();
	}

	private void setupDatabase() {
		String checkIndexQuery = "SHOW INDEX FROM questions WHERE Key_name = 'idx_ft_question_content'";
		List<?> result = jdbcTemplate.queryForList(checkIndexQuery);
		if (!result.isEmpty()) {
			jdbcTemplate.execute("ALTER TABLE questions DROP INDEX idx_ft_question_content");
		}
		jdbcTemplate.execute("ALTER TABLE questions ADD FULLTEXT INDEX idx_ft_question_content(content)");
	}

	private void initializeTestData() {
		content = "수시 원서 일정 알려주세요.";
		question = questionRepository.save(
			Question.of(content, "수시 일정", AdmissionType.SUSI, AdmissionCategory.ADMISSION_GUIDELINE));
		answer = answerRepository.save(Answer.of(question, "수시 일정은 2024년 12월 19일(목)부터 ..."));
		answerReferenceRepository.save(AnswerReference.of("테스트 title", "테스트 link", answer));
	}

	@Test
	void 질문_API() throws Exception {
		// given
		QuestionRequest request = new QuestionRequest(AdmissionType.SUSI, AdmissionCategory.ADMISSION_GUIDELINE,
			content);

		// when
		ResultActions resultActions = performPostRequest("/api/questions", request);

		// then
		verifyQuestionResponse(resultActions, content);
	}

	@Test
	void 질문_목록_조회_API() throws Exception {
		// given
		FindQuestionsRequest request = new FindQuestionsRequest(AdmissionType.SUSI,
			AdmissionCategory.ADMISSION_GUIDELINE);

		// when
		ResultActions resultActions = performGetFindQuestionsRequest("/api/questions", request);

		// then
		verifyQuestionListResponse(resultActions, content);
	}

	@Test
	void 질문_목록_조회_API_카테고리_없이() throws Exception {
		// given
		FindQuestionsRequest request = new FindQuestionsRequest(AdmissionType.SUSI, null);

		// when
		ResultActions resultActions = performGetFindQuestionsRequest("/api/questions", request);

		// then
		verifyQuestionListResponse(resultActions, content);
	}

	@Test
	void 질문_자동완성_API() throws Exception {
		// given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;
		SearchQuestionsRequest request = new SearchQuestionsRequest(type, category, "example content", 5, 0, 0L);

		// when
		ResultActions resultActions = performGetSearchQuestionsRequest("/api/questions/search", request);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").isArray());
	}

	@Test
	void 질문_생성_API_검증_오류_1000자_초과() throws Exception {
		// given
		String oversizedContent = "a".repeat(1001); // 1001자로 생성
		QuestionRequest request = new QuestionRequest(AdmissionType.SUSI, AdmissionCategory.ADMISSION_GUIDELINE,
			oversizedContent);

		// when
		ResultActions resultActions = performPostRequest("/api/questions", request);

		// then
		verifyValidationError(resultActions, "content", "크기가 0에서 1000 사이여야 합니다");
	}

	private ResultActions performPostRequest(String url, Object content) throws Exception {
		return mvc.perform(post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(content)))
			.andDo(print());
	}

	private ResultActions performGetFindQuestionsRequest(String url, FindQuestionsRequest request) throws Exception {
		return mvc.perform(get(url)
				.param("type", request.type().name())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private ResultActions performGetSearchQuestionsRequest(String url, SearchQuestionsRequest request) throws
		Exception {
		return mvc.perform(get(url)
				.param("content", request.content())
				.param("size", request.size().toString())
				.param("cursorViewCount", request.cursorViewCount().toString())
				.param("questionId", request.questionId().toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private void verifyQuestionResponse(ResultActions resultActions, String expectedContent) throws Exception {
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(expectedContent))
			.andExpect(jsonPath("$.answer.content").isNotEmpty())
			.andExpect(jsonPath("$.answer.renewalYear").isNotEmpty());
	}

	private void verifyQuestionListResponse(ResultActions resultActions, String expectedContent) throws Exception {
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].content").value(expectedContent))
			.andExpect(jsonPath("$[0].answer.content").isNotEmpty())
			.andExpect(jsonPath("$[0].answer.renewalYear").isNotEmpty());
	}

	private void verifyValidationError(ResultActions resultActions, String field, String expectedMessage) throws
		Exception {
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString(expectedMessage)))
			.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString(field)));
	}
}