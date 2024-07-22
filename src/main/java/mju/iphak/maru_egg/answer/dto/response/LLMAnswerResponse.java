package mju.iphak.maru_egg.answer.dto.response;

import lombok.Builder;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.question.domain.QuestionCategory;

@Builder
public record LLMAnswerResponse(
	String answer,
	QuestionCategory questionCategory
) {
	public static LLMAnswerResponse of(Answer answer, QuestionCategory category) {
		return LLMAnswerResponse.builder()
			.answer(answer.getContent())
			.questionCategory(category)
			.build();
	}
}