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

import mju.iphak.maru_egg.admission.application.detail.command.create.CreateAdmissionTypeDetail;
import mju.iphak.maru_egg.admission.application.status.command.init.InitAdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.common.IntegrationTest;

class AdmissionTypeDetailControllerTest extends IntegrationTest {

	@Autowired
	private CreateAdmissionTypeDetail createAdmissionTypeDetail;

	@Autowired
	private InitAdmissionTypeStatusService initAdmissionTypeStatusService;

	@BeforeEach
	void setUp() {
		initAdmissionTypeStatusService.invoke();
		createAdmissionTypeDetail.invoke("학교장추천전형", AdmissionType.SUSI);
	}

	@DisplayName("[성공] 전체 질문타입 상세 조회 요청")
	@Test
	void 전체_질문타입_상세_조회_성공() throws Exception {
		// when
		ResultActions resultActions = GetAllAdmissionTypeDetails();

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@DisplayName("[성공] 특정 타입의 질문타입 상세 조회 요청")
	@Test
	void 특정_타입의_질문타입_상세_조회_성공() throws Exception {
		// when
		ResultActions resultActions = GetSpecificAdmissionTypeDetails("SUSI");

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	private ResultActions GetAllAdmissionTypeDetails() throws Exception {
		return mvc.perform(get("/api/admissions/details")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private ResultActions GetSpecificAdmissionTypeDetails(String admissionType) throws Exception {
		return mvc.perform(get("/api/admissions/details/{admissionType}", admissionType)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}
}
