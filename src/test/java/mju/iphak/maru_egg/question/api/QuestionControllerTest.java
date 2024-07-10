package mju.iphak.maru_egg.question.api;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

class QuestionControllerTest extends IntegrationTest {

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private QuestionService questionService;

	@Test
	void 질문_API() throws Exception {
		// given
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
		String content = "content";
		Question question = Question.of(content, type, category);

		Answer answer = Answer.of(question, content);

		AnswerResponse answerResponse = AnswerResponse.from(answer);

		QuestionRequest request = new QuestionRequest(type, category, content);
		QuestionResponse response = QuestionResponse.of(question, answerResponse);

		// when
		when(questionService.getQuestionResponse(type, category, content)).thenReturn(response);
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

	private ResultActions requestCreateQuestion(QuestionRequest dto) throws Exception {
		return mvc.perform(post("/api/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andDo(print());
	}
}