package mju.iphak.maru_egg.answer.application.create;

import mju.iphak.maru_egg.question.dto.request.SaveRAGAnswerRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

public interface CreateRAGAnswer {

	QuestionResponse invoke(SaveRAGAnswerRequest request);
}
