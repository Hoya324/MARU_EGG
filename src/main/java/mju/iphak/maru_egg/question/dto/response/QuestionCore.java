package mju.iphak.maru_egg.question.dto.response;

import lombok.Builder;

@Builder
public record QuestionCore(
	Long id,
	String contentToken
) {
	public static QuestionCore of(Long id, String contentToken) {
		return QuestionCore.builder()
			.id(id)
			.contentToken(contentToken)
			.build();
	}
}
