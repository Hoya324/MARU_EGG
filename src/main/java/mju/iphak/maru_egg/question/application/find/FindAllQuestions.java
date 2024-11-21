package mju.iphak.maru_egg.question.application.find;

import java.util.List;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.question.dto.response.QuestionListItemResponse;

public interface FindAllQuestions {

	List<QuestionListItemResponse> invoke(final AdmissionType type, final AdmissionCategory category);
}
