package mju.iphak.maru_egg.admission.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.admission.api.swagger.AdminAdmissionTypeStatusControllerDocs;
import mju.iphak.maru_egg.admission.application.AdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.dto.request.UpdateAdmissionTypeStatusRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/questions/status")
public class AdminAdmissionTypeStatusController implements AdminAdmissionTypeStatusControllerDocs {

	private final AdmissionTypeStatusService admissionTypeStatusService;

	@PostMapping("/init")
	public void initializeQuestionTypeStatus() {
		admissionTypeStatusService.initializeQuestionTypeStatus();
	}

	@PutMapping()
	public void updateQuestionTypeStatus(@Valid @RequestBody UpdateAdmissionTypeStatusRequest request) {
		admissionTypeStatusService.updateStatus(request.type());
	}
}
