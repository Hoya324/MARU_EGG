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

import mju.iphak.maru_egg.admission.application.status.command.init.InitAdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.application.status.query.find.FindAdmissionTypeStatusService;
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

	@DisplayName("[성공] 전체 질문타입과 상태 조회 요청")
	@Test
	void 전체_질문타입과_상태_조회_성공() throws Exception {
		// when
		ResultActions resultActions = GetQuestionTypeStatus();

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	private ResultActions GetQuestionTypeStatus() throws Exception {
		return mvc.perform(get("/api/admissions/status")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}
}
