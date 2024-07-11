package mju.iphak.maru_egg.question.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.question.domain.Question;

@Builder
@Schema(description = "질문 응답 DTO", example = """
	{
	  "id": 1,
	  "dateInformation": "생성일자: 2023-07-11T10:00:00, 마지막 DB 갱신일자: 2023-07-11T12:00:00",
	  "answer": {
	    "id": 1,
	    "content": "입학 요강은 다음과 같습니다."
	    "renewalYear" : 2024,
	    "dateInformation": "생성일자: 2023-07-11T10:00:00, 마지막 DB 갱신일자: 2023-07-11T12:00:00"
	  }
	}
	""")
public record QuestionResponse(
	@Schema(description = "질문 id")
	Long id,

	@Schema(description = "답변 DB 생성 및 업데이트 날짜")
	String dateInformation,

	@Schema(description = "답변 DTO")
	AnswerResponse answer
) {

	public static QuestionResponse of(Question question, AnswerResponse answer) {
		return QuestionResponse.builder()
			.id(question.getId())
			.dateInformation(question.getDateInformation())
			.answer(answer)
			.build();
	}
}