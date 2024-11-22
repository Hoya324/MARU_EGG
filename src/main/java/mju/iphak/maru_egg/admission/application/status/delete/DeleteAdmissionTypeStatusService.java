package mju.iphak.maru_egg.admission.application.status.delete;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DeleteAdmissionTypeStatusService implements DeleteAdmissionTypeStatus {

	private final AdmissionTypeStatusRepository admissionTypeStatusRepository;

	public void invoke(AdmissionType type) {
		admissionTypeStatusRepository.deleteByAdmissionType(type);
	}

}
