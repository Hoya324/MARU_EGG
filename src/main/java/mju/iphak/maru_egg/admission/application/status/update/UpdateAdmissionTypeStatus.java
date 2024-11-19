package mju.iphak.maru_egg.admission.application.status.update;

import mju.iphak.maru_egg.admission.domain.AdmissionType;

public interface UpdateAdmissionTypeStatus {

	void invoke(final AdmissionType type);
}
