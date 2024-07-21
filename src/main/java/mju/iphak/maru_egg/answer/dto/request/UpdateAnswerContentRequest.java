package mju.iphak.maru_egg.answer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "답변 수정 요청 DTO", example = """
	{
	   "id": 601440912047263653,
	   "content": "변경된 답변입니다."
	 }
	""")
public record UpdateAnswerContentRequest(

	@Schema(description = "답변 ID")
	Long id,

	@Schema(description = "변경할 답변 내용")
	@NotBlank(message = "변경할 답변은 비어있을 수 없습니다.")
	String content
) {
}
