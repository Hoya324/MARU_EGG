package mju.iphak.maru_egg.admission.application.status.update;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAdmissionTypeStatusService implements UpdateAdmissionTypeStatus {

	private final AdmissionTypeStatusRepository admissionTypeStatusRepository;

	public void invoke(final AdmissionType type) {
		AdmissionTypeStatus admissionTypeStatus = admissionTypeStatusRepository.findByAdmissionType(type)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_ADMISSION_TYPE_STATUS.getMessage(), type)));
		admissionTypeStatus.updateStatus();
	}
}
