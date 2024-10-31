package mju.iphak.maru_egg.question.dao.request;

import lombok.Builder;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;

@Builder
public record SelectQuestionCores(
	AdmissionType type,
	AdmissionCategory category,
	String content,
	String contentToken
) {

	public static SelectQuestionCores of(final AdmissionType type, final AdmissionCategory category,
		final String content,
		final String contentToken) {
		return SelectQuestionCores.builder()
			.type(type)
			.category(category)
			.content(content)
			.contentToken(contentToken)
			.build();
	}
}
