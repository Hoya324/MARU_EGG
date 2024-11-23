package mju.iphak.maru_egg.admission.application.detail.command.create;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.admission.application.status.query.find.FindAdmissionTypeStatusService;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeDetail;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeDetailRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateAdmissionTypeDetailService implements CreateAdmissionTypeDetail {

	private final FindAdmissionTypeStatusService findAdmissionTypeStatusService;
	private final AdmissionTypeDetailRepository admissionTypeDetailRepository;

	public void invoke(String detail, AdmissionType type) {
		AdmissionTypeStatus admissionTypeStatus = findAdmissionTypeStatusService.invoke(type);
		AdmissionTypeDetail admissionTypeDetail = AdmissionTypeDetail.of(detail, admissionTypeStatus);
		admissionTypeDetailRepository.save(admissionTypeDetail);
	}
}
