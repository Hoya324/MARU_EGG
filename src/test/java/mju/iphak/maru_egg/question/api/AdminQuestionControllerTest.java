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

@WithMockUser(roles = "ADMIN")
class AdminQuestionControllerTest extends IntegrationTest {

	@MockBean
	private QuestionService questionService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void 질문_체크_API_정상적인_요청() throws Exception {
		// given
		CheckQuestionRequest request = new CheckQuestionRequest(1L, true);

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
		CheckQuestionRequest request = new CheckQuestionRequest(999L, true);
		doThrow(new EntityNotFoundException("질문을 찾을 수 없습니다.")).when(questionService)
			.checkQuestion(anyLong(), anyBoolean());

		// when
		ResultActions resultActions = performCheckQuestionRequest(request);

		// then
		resultActions.andExpect(status().isNotFound());
	}

	@Test
	void 질문_체크_API_서버_내부_오류() throws Exception {
		// given
		CheckQuestionRequest request = new CheckQuestionRequest(1L, true);
		doThrow(new RuntimeException("내부 서버 오류")).when(questionService)
			.checkQuestion(anyLong(), anyBoolean());

		// when
		ResultActions resultActions = performCheckQuestionRequest(request);

		// then
		resultActions.andExpect(status().isInternalServerError());
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

	private ResultActions performCheckQuestionRequest(CheckQuestionRequest request) throws Exception {
		return mvc.perform(post("/api/admin/questions/check")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());
	}

	private ResultActions performCheckQuestionRequest(String invalidJson) throws Exception {
		return mvc.perform(post("/api/admin/questions/check")
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

	private CreateQuestionRequest createSampleCreateQuestionRequest() {
		return new CreateQuestionRequest("example content", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE,
			new CreateAnswerRequest("example answer content", 2024));
	}
}