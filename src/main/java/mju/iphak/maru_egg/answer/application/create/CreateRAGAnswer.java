package mju.iphak.maru_egg.answer.application.create;

import mju.iphak.maru_egg.question.dto.request.SaveRAGAnswerRequest;

public interface CreateRAGAnswer {

	void invoke(SaveRAGAnswerRequest request);
}
