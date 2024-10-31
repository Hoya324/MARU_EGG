package mju.iphak.maru_egg.answer.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import mju.iphak.maru_egg.answer.application.AnswerManager;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.UpdateAnswerContentRequest;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@WithMockUser(roles = "ADMIN")
class AdminAnswerControllerTest extends IntegrationTest {

	@Autowired
	private AnswerManager answerManager;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@BeforeEach
	void setUp() {
		initializeTestData();
	}

	@AfterEach
	void tearDown() {
		answerRepository.deleteAllInBatch();
		questionRepository.deleteAllInBatch();
	}

	@Test
	void 답변_수정_API_정상적인_요청() throws Exception {
		// given
		Long questionId = getFirstQuestionId();
		UpdateAnswerContentRequest request = new UpdateAnswerContentRequest(questionId, "새로운 답변 내용");

		// when & then
		performRequestAndExpectStatus(request, status().isOk());
	}

	@Test
	void 답변_수정_API_잘못된_JSON_형식() throws Exception {
		// given
		String invalidJson = "{\"id\": \"ㅇㅇ\", \"content\": \"새로운 답변 내용\"}";

		// when & then
		performRequestAndExpectStatus(invalidJson, status().isBadRequest());
	}

	@Test
	void 답변_수정_API_존재하지_않는_답변_ID() throws Exception {
		// given
		UpdateAnswerContentRequest request = new UpdateAnswerContentRequest(999L, "새로운 답변 내용");

		// when & then
		performRequestAndExpectStatus(request, status().isNotFound());
	}

	private void performRequestAndExpectStatus(Object content, ResultMatcher expectedStatus) throws Exception {
		ResultActions resultActions = mvc.perform(put("/api/admin/answers")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(content)));

		resultActions.andExpect(expectedStatus).andDo(print());
	}

	private void performRequestAndExpectStatus(String content, ResultMatcher expectedStatus) throws Exception {
		ResultActions resultActions = mvc.perform(put("/api/admin/answers")
			.contentType(MediaType.APPLICATION_JSON)
			.content(content));

		resultActions.andExpect(expectedStatus).andDo(print());
	}

	private void initializeTestData() {
		Question question = Question.of("질문", "질문", AdmissionType.SUSI, AdmissionCategory.ADMISSION_GUIDELINE);
		Answer answer = Answer.of(question, "답변");

		questionRepository.saveAndFlush(question);
		answerRepository.saveAndFlush(answer);
	}

	private Long getFirstQuestionId() {
		List<Question> questions = questionRepository.findAll();
		return questions.get(0).getId();
	}
}