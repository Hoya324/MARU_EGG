package mju.iphak.maru_egg.question.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

@Schema(description = "질문 목록 요청 DTO")
@ParameterObject
public record FindQuestionsRequest(

	@Schema(description = "질문 타입(수시, 정시, 편입학, 재외국민)", allowableValues = {"SUSI", "JEONGSI", "PYEONIP", "JEAOEGUGMIN"})
	@NotNull(message = "질문 타입은 비어있을 수 없습니다.")
	QuestionType type,

	@Schema(description = "질문 카테고리(모집요강, 입시결과, 기출 문제)", allowableValues = {"ADMISSION_GUIDELINE", "PASSING_RESULT",
		"PAST_QUESTIONS", "INTERVIEW_PRACTICAL_TEST"})
	QuestionCategory category
) {
}