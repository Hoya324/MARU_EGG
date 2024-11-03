package mju.iphak.maru_egg.admission.api;

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

import mju.iphak.maru_egg.admission.application.AdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.dto.request.UpdateAdmissionTypeStatusRequest;
import mju.iphak.maru_egg.common.IntegrationTest;

@WithMockUser(roles = "ADMIN")
class AdminAdmissionTypeStatusControllerTest extends IntegrationTest {

	@Autowired
	private AdmissionTypeStatusService admissionTypeStatusService;

	@BeforeEach
	void setUp() {
		admissionTypeStatusService.initializeAdmissionTypeStatus();
		admissionTypeStatusService.deleteAdmissionTypeStatus(AdmissionType.JEONGSI);
	}

	@DisplayName("200 질문 상태 초기화")
	@Test
	public void 질문_상태_초기화_API_정상적인_요청() throws Exception {
		// given & when
		ResultActions resultActions = performInitializeAdmissionTypeStatus();

		// then
		resultActions.andExpect(status().isOk());

	}

	@DisplayName("200 질문타입 상태 변경")
	@Test
	public void 질문타입_상태_변경_API_정상적인_요청() throws Exception {
		// given
		UpdateAdmissionTypeStatusRequest request = new UpdateAdmissionTypeStatusRequest(AdmissionType.SUSI);

		// when
		ResultActions resultActions = performUpdateAdmissionTypeStatus(request);

		// then
		resultActions.andExpect(status().isOk());
	}

	@DisplayName("404 입학 전형 상태 변경")
	@Test
	public void 입학_전형_상태_변경_API_존재하지_않는_질문_타입() throws Exception {
		// given
		UpdateAdmissionTypeStatusRequest request = new UpdateAdmissionTypeStatusRequest(AdmissionType.JEONGSI);

		// when
		ResultActions resultActions = performUpdateAdmissionTypeStatus(request);

		// then
		resultActions.andExpect(status().isNotFound());
	}

	private ResultActions performInitializeAdmissionTypeStatus() throws Exception {
		return mvc.perform(post("/api/admin/questions/status/init")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());
	}

	private ResultActions performUpdateAdmissionTypeStatus(UpdateAdmissionTypeStatusRequest request) throws Exception {
		return mvc.perform(put("/api/admin/questions/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());
	}
}
