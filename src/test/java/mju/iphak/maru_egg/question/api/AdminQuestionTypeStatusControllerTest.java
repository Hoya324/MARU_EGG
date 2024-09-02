package mju.iphak.maru_egg.question.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.question.application.QuestionTypeStatusService;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.request.UpdateQuestionTypeStatusRequest;

@WithMockUser(roles = "ADMIN")
class AdminQuestionTypeStatusControllerTest extends IntegrationTest {

	@Autowired
	private QuestionTypeStatusService questionTypeStatusService;

	@BeforeEach
	void setUp() {
		questionTypeStatusService.initializeQuestionTypeStatus();
		questionTypeStatusService.deleteQuestionTypeStatus(QuestionType.JEONGSI);
	}

	@DisplayName("200 질문 상태 초기화")
	@Test
	public void 질문_상태_초기화_API_정상적인_요청() throws Exception {
		// given & when
		ResultActions resultActions = performInitializeQuestionTypeStatus();

		// then
		resultActions.andExpect(status().isOk());

	}

	@DisplayName("200 질문타입 상태 변경")
	@Test
	public void 질문타입_상태_변경_API_정상적인_요청() throws Exception {
		// given
		UpdateQuestionTypeStatusRequest request = new UpdateQuestionTypeStatusRequest(QuestionType.SUSI);

		// when
		ResultActions resultActions = performUpdateQuestionTypeStatus(request);

		// then
		resultActions.andExpect(status().isOk());
	}

	@DisplayName("404 질문타입 상태 변경")
	@Test
	public void 질문타입_상태_변경_API_존재하지_않는_질문_타입() throws Exception {
		// given
		UpdateQuestionTypeStatusRequest request = new UpdateQuestionTypeStatusRequest(QuestionType.JEONGSI);

		// when
		ResultActions resultActions = performUpdateQuestionTypeStatus(request);

		// then
		resultActions.andExpect(status().isNotFound());
	}

	private ResultActions performInitializeQuestionTypeStatus() throws Exception {
		return mvc.perform(post("/api/admin/questions/status/init")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private ResultActions performUpdateQuestionTypeStatus(UpdateQuestionTypeStatusRequest request) throws Exception {
		return mvc.perform(put("/api/admin/questions/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());
	}
}
