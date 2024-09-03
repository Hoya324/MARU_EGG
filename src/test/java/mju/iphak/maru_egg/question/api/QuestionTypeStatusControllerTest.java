package mju.iphak.maru_egg.question.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import mju.iphak.maru_egg.common.IntegrationTest;
import mju.iphak.maru_egg.question.application.QuestionTypeStatusService;

class QuestionTypeStatusControllerTest extends IntegrationTest {

	@Autowired
	private QuestionTypeStatusService questionTypeStatusService;

	@BeforeEach
	void setUp() {
		questionTypeStatusService.initializeQuestionTypeStatus();
	}

	@DisplayName("200 전체 질문타입과 상태 조회")
	@Test
	public void 전체_질문타입과_상태_조회_정상적인_요청() throws Exception {
		// given & when
		ResultActions resultActions = performGetQuestionTypeStatus();

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	private ResultActions performGetQuestionTypeStatus() throws Exception {
		return mvc.perform(get("/api/questions/status")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}
}