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

import mju.iphak.maru_egg.admission.application.AdmissionTypeDetailService;
import mju.iphak.maru_egg.admission.application.AdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.common.IntegrationTest;

class AdmissionTypeDetailControllerTest extends IntegrationTest {

	@Autowired
	private AdmissionTypeStatusService admissionTypeStatusService;

	@Autowired
	private AdmissionTypeDetailService admissionTypeDetailService;

	@BeforeEach
	void setUp() {
		admissionTypeStatusService.initializeAdmissionTypeStatus();
		admissionTypeDetailService.create("학교장추천전형", AdmissionType.SUSI);
	}

	@DisplayName("200 전체 질문타입 상세 조회")
	@Test
	void 전체_질문타입_상세_조회() throws Exception {
		ResultActions resultActions = mvc.perform(get("/api/admissions/details")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@DisplayName("200 특정 타입의 질문타입 상세 조회")
	@Test
	void 특정_타입의_질문타입_상세_조회() throws Exception {
		ResultActions resultActions = mvc.perform(get("/api/admissions/details/SUSI")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isNotEmpty());
	}
}
