package mju.iphak.maru_egg.question.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;

import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.application.QuestionProcessingService;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.request.FindQuestionsRequest;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.request.SearchQuestionsRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

@TestPropertySource(properties = {
	"web-client.base-url=http://localhost:8080",
	"JWT_SECRET=my_test_secret_key"
})
class QuestionControllerTest extends IntegrationTest {

	@MockBean
	private QuestionProcessingService questionProcessingService;

	@MockBean
	private QuestionService questionService;

	private Question question;
	private Answer answer;
	private QuestionType type;
	private QuestionCategory category;
	private String content;
	private String contentToken;

	@BeforeEach
	void setUp() {
		type = QuestionType.SUSI;
		category = QuestionCategory.ADMISSION_GUIDELINE;
		content = "content";
		contentToken = "content";
		question = Question.of(content, contentToken, type, category);
		answer = Answer.of(question, content);
	}

	@Test
	void 질문_API() throws Exception {
		// given
		AnswerResponse answerResponse = AnswerResponse.from(answer);

		QuestionRequest request = new QuestionRequest(type, category, content);
		QuestionResponse response = QuestionResponse.of(question, answerResponse);

		// when
		when(questionProcessingService.question(type, category, content)).thenReturn(response);
		ResultActions resultActions = requestCreateQuestion(request);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(question.getId()))
			.andExpect(jsonPath("$.dateInformation").value(question.getDateInformation()))
			.andExpect(jsonPath("$.answer.id").value(answerResponse.id()))
			.andExpect(jsonPath("$.answer.content").value(answerResponse.content()))
			.andExpect(jsonPath("$.answer.renewalYear").value(answerResponse.renewalYear()))
			.andExpect(jsonPath("$.answer.dateInformation").value(answerResponse.dateInformation()));
	}

	@Test
	void 질문_목록_조회_API() throws Exception {
		// given
		AnswerResponse answerResponse = AnswerResponse.from(answer);

		FindQuestionsRequest request = new FindQuestionsRequest(type, category);
		QuestionResponse response = QuestionResponse.of(question, answerResponse);

		// when
		when(questionService.getQuestions(type, category)).thenReturn(List.of(response));
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
		QuestionResponse response = QuestionResponse.of(question, answerResponse);

		// when
		when(questionService.getQuestions(type, null)).thenReturn(List.of(response));
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
		when(questionService.searchQuestionsOfCursorPaging(request.content(), request.cursorViewCount(),
			request.questionId(), request.size()))
			.thenReturn(response);
		ResultActions resultActions = requestSearchQuestions(request);

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data[0].content").value("example content"))
			.andExpect(jsonPath("$.hasNext").value(false));
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
}