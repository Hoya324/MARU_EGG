package mju.iphak.maru_egg.question.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "질문 목록 요청 DTO")
public record CheckQuestionRequest(

	@Parameter(description = "질문 id")
	Long questionId,

	@Parameter(description = "질문-답변 확인 상태")
	boolean check
) {
}
