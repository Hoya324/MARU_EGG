package mju.iphak.maru_egg.question.dao.request;

import org.springframework.data.domain.Pageable;

import lombok.Builder;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;

@Builder
public record SelectQuestions(
	AdmissionType type,
	AdmissionCategory category,
	String content,
	Integer cursorViewCount,
	Long questionId,
	Pageable pageable
) {
	public static SelectQuestions of(AdmissionType type,
		AdmissionCategory category,
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
