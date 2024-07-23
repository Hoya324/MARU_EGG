package mju.iphak.maru_egg.answer.dto.response;

import lombok.Builder;
import mju.iphak.maru_egg.answer.domain.Answer;

@Builder
public record LLMAnswerResponse(
	String questionType,
	String questionCategory,
	String answer
) {
	public static LLMAnswerResponse of(String questionType, String questionCategory, Answer answer) {
		return LLMAnswerResponse.builder()
			.questionType(questionType)
			.questionCategory(questionCategory)
			.answer(answer.getContent())
			.build();
	}
}