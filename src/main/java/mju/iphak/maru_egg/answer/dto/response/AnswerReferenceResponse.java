package mju.iphak.maru_egg.answer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AnswerReferenceResponse(

	@Schema(description = "참조 문서명", example = "2024 수시 모집요강.")
	String title,

	@Schema(description = "2024_수시_모집요강.pdf", example = "http://localhost/media/documents/수시_모집요강.pdf#page=1")
	String link
) {
	public static AnswerReferenceResponse of(String title, String link) {
		return AnswerReferenceResponse.builder()
			.title(title)
			.link(link)
			.build();
	}
}
