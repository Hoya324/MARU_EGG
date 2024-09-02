package mju.iphak.maru_egg.question.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.request.CheckQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@WithMockUser(roles = "ADMIN")
class AdminQuestionControllerTest extends IntegrationTest {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@BeforeEach
	void setUp() {
		initializeTestData();
	}

	@Test
	void 질문_체크_API_정상적인_요청() throws Exception {
		// given
		Long questionId = getFirstQuestionId();
		CheckQuestionRequest request = new CheckQuestionRequest(questionId);

		// when
		ResultActions resultActions = performCheckQuestionRequest(request);

		// then
		resultActions.andExpect(status().isOk());
	}

	@Test
	void 질문_체크_API_잘못된_JSON_형식() throws Exception {
		// given
		String invalidJson = "{\"questionId\": \"잘못된 아이디 형식\", \"check\": true}";

		// when
		ResultActions resultActions = performCheckQuestionRequest(invalidJson);

		// then
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	void 질문_체크_API_존재하지_않는_질문_ID() throws Exception {
		// given
		CheckQuestionRequest request = new CheckQuestionRequest(999L);

		// when
		ResultActions resultActions = performCheckQuestionRequest(request);

		// then
		resultActions.andExpect(status().isNotFound());
	}

	@Test
	void 질문_생성_API() throws Exception {
		// given
		CreateQuestionRequest request = createSampleCreateQuestionRequest();

		// when
		ResultActions resultActions = performCreateQuestionRequest(request);

		// then
		resultActions.andExpect(status().isOk());
	}

	@Test
	void 질문_삭제_API() throws Exception {
		// given
		Long id = getFirstQuestionId();

		// when
		ResultActions resultActions = performDeleteQuestionRequest(id);

		// then
		resultActions.andExpect(status().isOk());
	}

	private Long getFirstQuestionId() {
		List<Question> questions = questionRepository.findAll();
		return questions.get(0).getId();
	}

	private ResultActions performCheckQuestionRequest(CheckQuestionRequest request) throws Exception {
		return mvc.perform(put("/api/admin/questions/check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());
	}

	private ResultActions performCheckQuestionRequest(String invalidJson) throws Exception {
		return mvc.perform(put("/api/admin/questions/check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
			.andDo(print());
	}

	private ResultActions performCreateQuestionRequest(CreateQuestionRequest request) throws Exception {
		return mvc.perform(post("/api/admin/questions/new")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private ResultActions performDeleteQuestionRequest(Long id) throws Exception {
		return mvc.perform(delete("/api/admin/questions/" + id)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private CreateQuestionRequest createSampleCreateQuestionRequest() {
		return new CreateQuestionRequest("example content", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE,
			new CreateAnswerRequest("example answer content", 2024));
	}

	private void initializeTestData() {
		Question question = Question.of("질문", "질문", QuestionType.SUSI, QuestionCategory.ADMISSION_GUIDELINE);
		Answer answer = Answer.of(question, "답변");

		questionRepository.saveAndFlush(question);
		answerRepository.saveAndFlush(answer);
	}
}