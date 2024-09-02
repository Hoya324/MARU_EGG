package mju.iphak.maru_egg.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import mju.iphak.maru_egg.question.domain.QuestionType;

@Schema(description = "질문 목록 요청 DTO")
public record UpdateQuestionTypeStatusRequest(

	@Schema(description = "질문 타입(수시, 정시, 편입학)", allowableValues = {"SUSI", "JEONGSI", "PYEONIP"})
	QuestionType type
) {
}