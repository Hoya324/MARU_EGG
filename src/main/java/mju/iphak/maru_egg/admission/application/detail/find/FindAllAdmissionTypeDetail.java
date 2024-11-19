package mju.iphak.maru_egg.admission.application.detail.find;

import java.util.List;

import mju.iphak.maru_egg.admission.dto.response.AdmissionTypeDetailResponse;

public interface FindAllAdmissionTypeDetail {

	List<AdmissionTypeDetailResponse> invoke();
}
