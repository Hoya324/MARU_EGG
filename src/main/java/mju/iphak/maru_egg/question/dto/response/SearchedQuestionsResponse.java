package mju.iphak.maru_egg.question.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "질문 응답 DTO")
public record SearchedQuestionsResponse(

	@Schema(description = "질문 ID", example = "1")
	Long id,

	@Schema(description = "질문 내용", example = "수시 일정 알려주세요.")
	String content
) {
	public static SearchedQuestionsResponse of(Long id, String content) {
		return SearchedQuestionsResponse.builder()
			.id(id)
			.content(content)
			.build();
	}
}
