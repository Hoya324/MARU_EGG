package mju.iphak.maru_egg.answer.application.rag;

import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import reactor.core.publisher.Mono;

public interface RagAnswer {

	Mono<LLMAnswerResponse> invoke(LLMAskQuestionRequest request);
}
