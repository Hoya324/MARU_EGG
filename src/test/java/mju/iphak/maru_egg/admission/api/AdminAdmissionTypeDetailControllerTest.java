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

import mju.iphak.maru_egg.admission.application.detail.command.create.CreateAdmissionTypeDetailService;
import mju.iphak.maru_egg.admission.application.detail.query.find.FindAllAdmissionTypeDetailService;
import mju.iphak.maru_egg.admission.application.status.command.init.InitAdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.application.status.query.find.FindAdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.dto.request.CreateAdmissionTypeDetailRequest;
import mju.iphak.maru_egg.admission.dto.request.UpdateAdmissionTypeDetailRequest;
import mju.iphak.maru_egg.common.IntegrationTest;

@WithMockUser(roles = "ADMIN")
class AdminAdmissionTypeDetailControllerTest extends IntegrationTest {

	@Autowired
	private FindAdmissionTypeStatusService findAdmissionTypeStatusService;

	@Autowired
	private FindAllAdmissionTypeDetailService findAllAdmissionTypeDetail;

	@Autowired
	private CreateAdmissionTypeDetailService createAdmissionTypeDetail;

	@Autowired
	private InitAdmissionTypeStatusService initAdmissionTypeStatus;

	@BeforeEach
	void setUp() {
		initAdmissionTypeStatus.invoke();
		createAdmissionTypeDetail.invoke("학교장추천전형", AdmissionType.SUSI);
	}

	@DisplayName("POST /api/admin/admissions/detail - 전형 유형 세부정보 생성")
	@Test
	void createAdmissionTypeDetail() throws Exception {
		// given
		CreateAdmissionTypeDetailRequest request = new CreateAdmissionTypeDetailRequest("New Detail",
			AdmissionType.SUSI);

		// when
		ResultActions result = mvc.perform(post("/api/admin/admissions/detail")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print());

		// then
		result.andExpect(status().isOk());
	}

	@DisplayName("PUT /api/admin/admissions/{id} - 전형 유형 세부정보 수정")
	@Test
	void updateAdmissionTypeDetail() throws Exception {
		// given
		Long admissionTypeDetailId = findAllAdmissionTypeDetail.invoke().get(0).id();
		String updatedName = "UpdatedName";
		UpdateAdmissionTypeDetailRequest updateAdmissionTypeDetailRequest = new UpdateAdmissionTypeDetailRequest(
			updatedName);

		// when
		ResultActions result = mvc.perform(put("/api/admin/admissions/{id}", admissionTypeDetailId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateAdmissionTypeDetailRequest)))
			.andDo(print());

		// then
		result.andExpect(status().isOk());
	}

	@DisplayName("DELETE /api/admin/admissions/{id} - 전형 유형 세부정보 삭제")
	@Test
	void deleteAdmissionTypeDetail() throws Exception {
		// given
		Long admissionTypeDetailId = findAllAdmissionTypeDetail.invoke().get(0).id();

		// when
		ResultActions result = mvc.perform(delete("/api/admin/admissions/" + admissionTypeDetailId)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print());

		// then
		result.andExpect(status().isOk());
	}
}
