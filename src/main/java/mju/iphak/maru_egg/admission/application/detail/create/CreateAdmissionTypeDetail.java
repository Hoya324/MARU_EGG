package mju.iphak.maru_egg.admission.application.detail.create;

import mju.iphak.maru_egg.admission.domain.AdmissionType;

public interface CreateAdmissionTypeDetail {

	void invoke(String detail, AdmissionType type);
}
