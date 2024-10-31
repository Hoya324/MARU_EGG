package mju.iphak.maru_egg.admission.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.admission.api.swagger.AdminAdmissionTypeDetailControllerDocs;
import mju.iphak.maru_egg.admission.application.AdmissionTypeDetailService;
import mju.iphak.maru_egg.admission.dto.request.CreateAdmissionTypeDetailRequest;

@RestController
@RequestMapping("/api/admin/admissions")
@RequiredArgsConstructor
public class AdminAdmissionTypeDetailController implements AdminAdmissionTypeDetailControllerDocs {

	private final AdmissionTypeDetailService admissionTypeDetailService;

	@PostMapping("/detail")
	public void create(@RequestBody CreateAdmissionTypeDetailRequest request) {
		admissionTypeDetailService.create(request.detail(), request.type());
	}

	@PutMapping("/{id}")
	public void update(@PathVariable("id") Long admissionTypeDetailId, @RequestBody String name) {
		admissionTypeDetailService.update(admissionTypeDetailId, name);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Long id) {
		admissionTypeDetailService.delete(id);
	}
}
