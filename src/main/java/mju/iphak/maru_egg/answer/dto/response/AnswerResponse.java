package mju.iphak.maru_egg.answer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import mju.iphak.maru_egg.answer.domain.Answer;

@Builder
@Schema(description = "답변 DTO")
public record AnswerResponse(
	@Schema(description = "답변 id")
	Long id,

	@Schema(description = "답변 내용")
	String content,

	@Schema(description = "답변 갱신 년도")
	int renewalYear,

	@Schema(description = "답변 DB 생성 및 업데이트 날짜")
	String dateInformation
) {
	public static AnswerResponse from(Answer answer) {
		return AnswerResponse.builder()
			.id(answer.getId())
			.content(answer.getContent())
			.renewalYear(answer.getRenewalYear())
			.dateInformation(answer.getDateInformation())
			.build();
	}
}
