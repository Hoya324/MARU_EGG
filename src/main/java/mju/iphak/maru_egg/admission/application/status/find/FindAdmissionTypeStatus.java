package mju.iphak.maru_egg.admission.application.status.find;

import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;

public interface FindAdmissionTypeStatus {

	AdmissionTypeStatus invoke(AdmissionType type);
}
