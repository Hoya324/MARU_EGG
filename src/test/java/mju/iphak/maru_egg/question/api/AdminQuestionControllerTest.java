package mju.iphak.maru_egg.question.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.request.CheckQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;

class AdminQuestionControllerTest extends IntegrationTest {

	@MockBean
	private QuestionService questionService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 질문_체크_API_정상적인_요청() throws Exception {
		// given
		CheckQuestionRequest request = new CheckQuestionRequest(1L, true);

		// when
		ResultActions resultActions = mvc.perform(post("/api/admin/questions/check")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		resultActions
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 질문_체크_API_잘못된_JSON_형식() throws Exception {
		// given
		String invalidJson = "{\"questionId\": \"잘못된 아이디 형식\", \"check\": true}";

		// when
		ResultActions resultActions = mvc.perform(post("/api/admin/questions/check")
			.contentType(MediaType.APPLICATION_JSON)
			.content(invalidJson));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andDo(print());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 질문_체크_API_존재하지_않는_질문_ID() throws Exception {
		// given
		CheckQuestionRequest request = new CheckQuestionRequest(999L, true);
		doThrow(new EntityNotFoundException("질문을 찾을 수 없습니다.")).when(questionService)
			.checkQuestion(anyLong(), anyBoolean());

		// when
		ResultActions resultActions = mvc.perform(post("/api/admin/questions/check")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andDo(print());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 질문_체크_API_서버_내부_오류() throws Exception {
		// given
		CheckQuestionRequest request = new CheckQuestionRequest(1L, true);
		doThrow(new RuntimeException("내부 서버 오류")).when(questionService).checkQuestion(anyLong(), anyBoolean());

		// when
		ResultActions resultActions = mvc.perform(post("/api/admin/questions/check")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		resultActions
			.andExpect(status().isInternalServerError())
			.andDo(print());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
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

	private ResultActions requestCreateQuestion(CreateQuestionRequest dto) throws Exception {
		return mvc.perform(post("/api/admin/questions/new")
				.content(objectMapper.writeValueAsString(dto))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}
}