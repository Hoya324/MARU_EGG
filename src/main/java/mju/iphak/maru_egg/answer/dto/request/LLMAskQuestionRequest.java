package mju.iphak.maru_egg.answer.dto.request;

import lombok.Builder;

@Builder
public record LLMAskQuestionRequest(
	String questionType,
	String questionCategory,
	String question
) {
	public static LLMAskQuestionRequest of(String questionType, String questionCategory, String question) {
		return LLMAskQuestionRequest.builder()
			.questionType(questionType)
			.questionCategory(questionCategory)
			.question(question)
			.build();
	}
}
