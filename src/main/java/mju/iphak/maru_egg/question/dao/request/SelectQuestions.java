package mju.iphak.maru_egg.question.dao.request;

import org.springframework.data.domain.Pageable;

import lombok.Builder;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

@Builder
public record SelectQuestions(
	QuestionType type,
	QuestionCategory category,
	String content,
	Integer cursorViewCount,
	Long questionId,
	Pageable pageable
) {
	public static SelectQuestions of(QuestionType type,
		QuestionCategory category,
		String content,
		Integer cursorViewCount,
		Long questionId,
		Pageable pageable) {
		return SelectQuestions.builder()
			.type(type)
			.category(category)
			.content(content)
			.cursorViewCount(cursorViewCount)
			.questionId(questionId)
			.pageable(pageable)
			.build();
	}
}
