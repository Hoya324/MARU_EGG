package mju.iphak.maru_egg.question.dto.request;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "질문 자동완성 요청 DTO")
@ParameterObject
public record SearchQuestionsRequest(
	@Schema(description = "질문 내용", example = "수시")
	@NotBlank(message = "질문은 비어있을 수 없습니다.")
	String content,

	@Parameter(description = "질문 수", example = "10")
	@PositiveOrZero
	Integer size,

	@Parameter(description = "질문 조회 수 cursor 식별값(초기 요청의 경우 0으로 입력, 이후엔 response 값의 nextCursorViewCount 사용)", example = "0")
	@PositiveOrZero
	Integer cursorViewCount,

	@Parameter(description = "질문 ID(초기 요청의 경우 0으로 입력, 이후엔 response 값의 nextQuestionId 사용)", example = "0")
	@PositiveOrZero
	Long questionId
) {
}
