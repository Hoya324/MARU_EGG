package mju.iphak.maru_egg.question.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "질문 응답 DTO", example = """
	{
	  "id": 600697396846981500,
	  "content": "수시 일정 알려주세요."
	}
	""")
public record SearchedQuestionsResponse(

	@Schema(description = "질문 ID")
	Long id,

	@Schema(description = "질문 내용")
	String content
) {
	public static SearchedQuestionsResponse of(Long id, String content) {
		return SearchedQuestionsResponse.builder()
			.id(id)
			.content(content)
			.build();
	}
}
