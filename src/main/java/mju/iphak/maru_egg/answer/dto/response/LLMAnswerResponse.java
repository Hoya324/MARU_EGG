package mju.iphak.maru_egg.answer.dto.response;

import lombok.Builder;
import mju.iphak.maru_egg.answer.domain.Answer;

@Builder
public record LLMAnswerResponse(
	String answer
) {
	public static LLMAnswerResponse from(Answer answer) {
		return LLMAnswerResponse.builder()
			.answer(answer.getContent())
			.build();
	}
}