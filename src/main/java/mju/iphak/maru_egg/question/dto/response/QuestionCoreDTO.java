package mju.iphak.maru_egg.question.dto.response;

import lombok.Builder;

@Builder
public record QuestionCoreDTO(
	Long id,
	String contentToken
) {
	public static QuestionCoreDTO of(Long id, String contentToken) {
		return QuestionCoreDTO.builder()
			.id(id)
			.contentToken(contentToken)
			.build();
	}
}
