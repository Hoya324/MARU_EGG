package mju.iphak.maru_egg.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

@Schema(description = "질문 생성 요청 DTO")
public record QuestionRequest(

	@Schema(description = "질문 타입(수시, 정시, 편입학)", allowableValues = {"SUSI", "JEONGSI", "PYEONIP"})
	@NotNull(message = "질문 타입은 비어있을 수 없습니다.")
	QuestionType type,

	@Schema(description = "질문 카테고리(모집요강, 입시결과, 기출 문제)", allowableValues = {"ADMISSION_GUIDELINE", "PASSING_RESULT",
		"PAST_QUESTIONS", "INTERVIEW_PRACTICAL_TEST"}, nullable = true)
	QuestionCategory category,

	@Schema(description = "질문 내용", example = "수시 입학 요강에 대해 알려주세요.")
	@NotBlank(message = "질문은 비어있을 수 없습니다.")
	@Size(max = 1000, message = "크기가 0에서 1000 사이여야 합니다.")
	String content
) {
}