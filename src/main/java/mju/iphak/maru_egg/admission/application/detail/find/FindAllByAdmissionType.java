package mju.iphak.maru_egg.admission.application.detail.find;

import java.util.List;

import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.dto.response.AdmissionTypeDetailResponse;

public interface FindAllByAdmissionType {

	List<AdmissionTypeDetailResponse> invoke(AdmissionType admissionType);
}
