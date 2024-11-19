package mju.iphak.maru_egg.admission.application.detail.update;

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
public class UpdateAdmissionTypeDetailService implements UpdateAdmissionTypeDetail {

	private final AdmissionTypeDetailRepository admissionTypeDetailRepository;

	public void invoke(Long admissionTypeDetailId, String name) {
		AdmissionTypeDetail admissionTypeDetail = admissionTypeDetailRepository.findById(admissionTypeDetailId)
			.orElseThrow(
				() -> new EntityNotFoundException(
					String.format(NOT_FOUND_ADMISSION_TYPE_DETAILS.getMessage(), admissionTypeDetailId)));
		admissionTypeDetail.updateDetailName(name);
	}

}
