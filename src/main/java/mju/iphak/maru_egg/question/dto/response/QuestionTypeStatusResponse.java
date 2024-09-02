package mju.iphak.maru_egg.question.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import mju.iphak.maru_egg.question.domain.QuestionType;

@Builder
public record QuestionTypeStatusResponse(
	@Schema(description = "질문 타입(수시, 정시, 편입학)", allowableValues = {"SUSI", "JEONGSI", "PYEONIP"})
	QuestionType type,

	@Schema(description = "활성화된 질문타입 (true면 활성화, false면 비활성화)", example = "true")
	boolean isActivated
) {
	public static QuestionTypeStatusResponse of(QuestionType type, boolean isActivated) {
		return QuestionTypeStatusResponse.builder()
			.type(type)
			.isActivated(isActivated)
			.build();
	}
}
