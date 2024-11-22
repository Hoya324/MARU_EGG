package mju.iphak.maru_egg.question.application.process;

import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

public interface ProcessQuestion {

	QuestionResponse invoke(final QuestionRequest request);
}
