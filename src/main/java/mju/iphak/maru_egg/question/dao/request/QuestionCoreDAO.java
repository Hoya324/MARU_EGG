package mju.iphak.maru_egg.question.dao.request;

import lombok.Builder;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;

@Builder
public record QuestionCoreDAO(
	AdmissionType type,
	AdmissionCategory category,
	String content,
	String contentToken
) {

	public static QuestionCoreDAO of(final QuestionRequest request, final String contentToken) {
		return QuestionCoreDAO.builder()
			.type(request.type())
			.category(request.category())
			.content(request.content())
			.contentToken(contentToken)
			.build();
	}
}
