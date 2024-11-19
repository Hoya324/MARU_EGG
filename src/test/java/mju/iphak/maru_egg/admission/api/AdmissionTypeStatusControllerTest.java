package mju.iphak.maru_egg.admission.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import mju.iphak.maru_egg.admission.application.status.find.FindAdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.application.status.init.InitAdmissionTypeStatusService;
import mju.iphak.maru_egg.common.IntegrationTest;

class AdmissionTypeStatusControllerTest extends IntegrationTest {

	@Autowired
	private FindAdmissionTypeStatusService findAdmissionTypeStatusService;

	@Autowired
	private InitAdmissionTypeStatusService initAdmissionTypeStatusService;

	@BeforeEach
	void setUp() {
		initAdmissionTypeStatusService.invoke();
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
		return mvc.perform(get("/api/admissions/status")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}
}