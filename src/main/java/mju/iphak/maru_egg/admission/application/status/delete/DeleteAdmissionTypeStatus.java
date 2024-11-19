package mju.iphak.maru_egg.admission.application.status.delete;

import mju.iphak.maru_egg.admission.domain.AdmissionType;

public interface DeleteAdmissionTypeStatus {

	void invoke(AdmissionType type);
}
