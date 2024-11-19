package mju.iphak.maru_egg.admission.application.status.find;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FindAdmissionTypeStatusService implements FindAdmissionTypeStatus {

	private final AdmissionTypeStatusRepository admissionTypeStatusRepository;

	public AdmissionTypeStatus invoke(AdmissionType type) {
		return admissionTypeStatusRepository.findByAdmissionType(type)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_ADMISSION_TYPE_STATUS.getMessage(), type.getType())));
	}
}
