package mju.iphak.maru_egg.question.application.create;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.question.domain.Question;

public interface CreateQuestionByTypeAndCategory {

	Question invoke(AdmissionType type, AdmissionCategory category, String content, String contentToken);
}
