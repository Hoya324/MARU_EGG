package mju.iphak.maru_egg.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

@Schema(description = "질문 생성 요청 DTO")
public record QuestionRequest(

	@Schema(description = "질문 타입(수시, 정시, 편입학, 재외국민)")
	QuestionType type,

	@Schema(description = "질문 카테고리(모집요강, 입시결과, 기출 문제)")
	QuestionCategory category,

	@Schema(description = "질문 내용")
	String content
) {
}
