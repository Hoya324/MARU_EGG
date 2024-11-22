package mju.iphak.maru_egg.answer.application.process;

import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

public interface ProcessAnswer {

	QuestionResponse invoke(QuestionRequest questionRequest, String contentToken);
}
