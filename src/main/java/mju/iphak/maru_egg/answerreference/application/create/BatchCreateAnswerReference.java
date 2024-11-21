package mju.iphak.maru_egg.answerreference.application.create;

import java.util.List;

import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answerreference.dto.response.AnswerReferenceResponse;

public interface BatchCreateAnswerReference {

	void invoke(Answer answer, List<AnswerReferenceResponse> referenceResponses);
}
