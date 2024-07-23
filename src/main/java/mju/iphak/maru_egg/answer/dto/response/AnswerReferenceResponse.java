package mju.iphak.maru_egg.answer.dto.response;

import lombok.Builder;

@Builder
public record AnswerReferenceResponse(
	String title,
	String link
) {
	public static AnswerReferenceResponse of(String title, String link) {
		return AnswerReferenceResponse.builder()
			.title(title)
			.link(link)
			.build();
	}
}
