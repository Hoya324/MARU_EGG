package mju.iphak.maru_egg.answer.api;

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
import mju.iphak.maru_egg.answer.application.AnswerManager;
import mju.iphak.maru_egg.answer.dto.request.UpdateAnswerContentRequest;
import mju.iphak.maru_egg.common.IntegrationTest;

class AdminAnswerControllerTest extends IntegrationTest {

	@MockBean
	private AnswerManager answerManager;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 답변_수정_API_정상적인_요청() throws Exception {
		// given
		UpdateAnswerContentRequest request = new UpdateAnswerContentRequest(1L, "새로운 답변 내용");

		// when
		ResultActions resultActions = mvc.perform(post("/api/admin/answers")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		resultActions
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 답변_수정_API_잘못된_JSON_형식() throws Exception {
		// given
		String invalidJson = "{\"id\": \"ㅇㅇ\", \"content\": \"새로운 답변 내용\"}";

		// when
		ResultActions resultActions = mvc.perform(post("/api/admin/answers")
			.contentType(MediaType.APPLICATION_JSON)
			.content(invalidJson));

		// then
		resultActions
			.andExpect(status().isBadRequest())
			.andDo(print());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 답변_수정_API_존재하지_않는_답변_ID() throws Exception {
		// given
		UpdateAnswerContentRequest request = new UpdateAnswerContentRequest(999L, "새로운 답변 내용");
		doThrow(new EntityNotFoundException("답변을 찾을 수 없습니다.")).when(answerManager)
			.updateAnswerContent(anyLong(), anyString());

		// when
		ResultActions resultActions = mvc.perform(post("/api/admin/answers")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		resultActions
			.andExpect(status().isNotFound())
			.andDo(print());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void 답변_수정_API_서버_내부_오류() throws Exception {
		// given
		UpdateAnswerContentRequest request = new UpdateAnswerContentRequest(1L, "새로운 답변 내용");
		doThrow(new RuntimeException("내부 서버 오류")).when(answerManager).updateAnswerContent(anyLong(), anyString());

		// when
		ResultActions resultActions = mvc.perform(post("/api/admin/answers")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(request)));

		// then
		resultActions
			.andExpect(status().isInternalServerError())
			.andDo(print());
	}
}