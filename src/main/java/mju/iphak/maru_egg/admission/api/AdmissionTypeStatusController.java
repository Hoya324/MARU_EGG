package mju.iphak.maru_egg.admission.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.admission.api.swagger.AdmissionTypeStatusControllerDocs;
import mju.iphak.maru_egg.admission.application.AdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.dto.response.AdmissionTypeStatusResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponses;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admissions/status")
public class AdmissionTypeStatusController implements AdmissionTypeStatusControllerDocs {

	private final AdmissionTypeStatusService admissionTypeStatusService;

	@CustomApiResponses({
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@GetMapping()
	public List<AdmissionTypeStatusResponse> getQuestionTypeStatus() {
		return admissionTypeStatusService.getAdmissionTypeStatus();
	}
}
