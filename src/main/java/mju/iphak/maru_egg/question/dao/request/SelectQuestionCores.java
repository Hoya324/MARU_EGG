package mju.iphak.maru_egg.question.dao.request;

import lombok.Builder;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

@Builder
public record SelectQuestionCores(
	QuestionType type,
	QuestionCategory category,
	String content,
	String contentToken
) {

	public static SelectQuestionCores of(final QuestionType type, final QuestionCategory category, final String content,
		final String contentToken) {
		return SelectQuestionCores.builder()
			.type(type)
			.category(category)
			.content(content)
			.contentToken(contentToken)
			.build();
	}
}
