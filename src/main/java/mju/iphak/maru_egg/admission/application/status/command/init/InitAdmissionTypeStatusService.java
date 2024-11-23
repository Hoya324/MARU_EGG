package mju.iphak.maru_egg.admission.application.status.command.init;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class InitAdmissionTypeStatusService implements InitAdmissionTypeStatus {

	private final AdmissionTypeStatusRepository admissionTypeStatusRepository;

	public void invoke() {
		List<AdmissionTypeStatus> admissionTypeStatuses = AdmissionTypeStatus.initialize();
		if (!isDatabaseEmpty()) {
			admissionTypeStatusRepository.deleteAll();
		}
		admissionTypeStatusRepository.saveAll(admissionTypeStatuses);
	}

	private boolean isDatabaseEmpty() {
		return admissionTypeStatusRepository.count() == 0;
	}
}
