package mju.iphak.maru_egg.answer.application.process;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

public interface ProcessAnswer {

	QuestionResponse invoke(AdmissionType type, AdmissionCategory category, String content, String contentToken);
}
