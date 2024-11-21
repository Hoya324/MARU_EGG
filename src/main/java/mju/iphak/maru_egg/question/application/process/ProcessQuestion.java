package mju.iphak.maru_egg.question.application.process;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

public interface ProcessQuestion {

	QuestionResponse invoke(final AdmissionType type, final AdmissionCategory category, final String content);
}
