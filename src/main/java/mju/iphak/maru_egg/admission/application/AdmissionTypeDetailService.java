package mju.iphak.maru_egg.admission.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeDetail;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.dto.response.AdmissionTypeDetailResponse;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeDetailRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AdmissionTypeDetailService {

	private final AdmissionTypeStatusService admissionTypeStatusService;
	private final AdmissionTypeDetailRepository admissionTypeDetailRepository;

	public void create(String detail, AdmissionType type) {
		AdmissionTypeStatus admissionTypeStatus = admissionTypeStatusService.findByAdmissionType(type);
		AdmissionTypeDetail admissionTypeDetail = AdmissionTypeDetail.of(detail, admissionTypeStatus);
		admissionTypeDetailRepository.save(admissionTypeDetail);
	}

	public List<AdmissionTypeDetailResponse> findAllByAdmissionType(AdmissionType admissionType) {
		List<AdmissionTypeDetail> admissionTypeDetails = admissionTypeDetailRepository.findAllByAdmissionType(
			admissionType);
		return admissionTypeDetails.stream()
			.map(AdmissionTypeDetailResponse::from)
			.toList();
	}

	public List<AdmissionTypeDetailResponse> findAll() {
		List<AdmissionTypeDetail> admissionTypeDetails = admissionTypeDetailRepository.findAll();
		return admissionTypeDetails.stream()
			.map(AdmissionTypeDetailResponse::from)
			.toList();
	}

	public AdmissionTypeDetailResponse findOne(Long id) {
		AdmissionTypeDetail admissionTypeDetail = admissionTypeDetailRepository.findById(id)
			.orElseThrow(
				() -> new EntityNotFoundException(String.format(NOT_FOUND_QUESTION_TYPE_DETAILS.getMessage(), id)));
		return AdmissionTypeDetailResponse.from(admissionTypeDetail);
	}

	public void update(Long admissionTypeDetailId, String name) {
		AdmissionTypeDetail admissionTypeDetail = admissionTypeDetailRepository.findById(admissionTypeDetailId)
			.orElseThrow(
				() -> new EntityNotFoundException(
					String.format(NOT_FOUND_QUESTION_TYPE_DETAILS.getMessage(), admissionTypeDetailId)));
		admissionTypeDetail.updateDetailName(name);
	}

	public void delete(Long id) {
		AdmissionTypeDetail admissionTypeDetail = admissionTypeDetailRepository.findById(id)
			.orElseThrow(
				() -> new EntityNotFoundException(String.format(NOT_FOUND_QUESTION_TYPE_DETAILS.getMessage(), id)));
		admissionTypeDetailRepository.delete(admissionTypeDetail);
	}
}
