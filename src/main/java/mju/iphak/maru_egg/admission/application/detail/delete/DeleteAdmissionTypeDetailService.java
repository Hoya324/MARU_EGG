package mju.iphak.maru_egg.admission.application.detail.delete;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeDetail;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeDetailRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteAdmissionTypeDetailService implements DeleteAdmissionTypeDetail {

	private final AdmissionTypeDetailRepository admissionTypeDetailRepository;

	public void invoke(Long id) {
		AdmissionTypeDetail admissionTypeDetail = admissionTypeDetailRepository.findById(id)
			.orElseThrow(
				() -> new EntityNotFoundException(String.format(NOT_FOUND_ADMISSION_TYPE_DETAILS.getMessage(), id)));
		admissionTypeDetailRepository.delete(admissionTypeDetail);
	}
}
