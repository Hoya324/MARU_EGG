package mju.iphak.maru_egg.admission.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.dto.response.AdmissionTypeStatusResponse;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AdmissionTypeStatusService {

	private final AdmissionTypeStatusRepository admissionTypeStatusRepository;

	public void initializeQuestionTypeStatus() {
		List<AdmissionTypeStatus> admissionTypeStatuses = AdmissionTypeStatus.initialize();
		if (!isDatabaseEmpty()) {
			admissionTypeStatusRepository.deleteAll();
		}
		admissionTypeStatusRepository.saveAll(admissionTypeStatuses);
	}

	public void updateStatus(final AdmissionType type) {
		AdmissionTypeStatus admissionTypeStatus = admissionTypeStatusRepository.findByAdmissionType(type)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_TYPE_STATUS.getMessage(), type)));
		admissionTypeStatus.updateStatus();
	}

	public List<AdmissionTypeStatusResponse> getQuestionTypeStatus() {
		List<AdmissionTypeStatus> admissionTypeStatuses = admissionTypeStatusRepository.findAll();
		return admissionTypeStatuses.stream()
			.map(questionTypeStatus -> AdmissionTypeStatusResponse.of(questionTypeStatus.getAdmissionType(),
				questionTypeStatus.isActivated()))
			.toList();
	}

	public void deleteQuestionTypeStatus(AdmissionType type) {
		admissionTypeStatusRepository.deleteByAdmissionType(type);
	}

	private boolean isDatabaseEmpty() {
		return admissionTypeStatusRepository.count() == 0;
	}
}
