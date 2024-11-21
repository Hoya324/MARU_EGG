package mju.iphak.maru_egg.question.dao.request;

import lombok.Builder;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;

@Builder
public record QuestionCoreDAO(
	AdmissionType type,
	AdmissionCategory category,
	String content,
	String contentToken
) {

	public static QuestionCoreDAO of(final AdmissionType type, final AdmissionCategory category,
		final String content,
		final String contentToken) {
		return QuestionCoreDAO.builder()
			.type(type)
			.category(category)
			.content(content)
			.contentToken(contentToken)
			.build();
	}
}
