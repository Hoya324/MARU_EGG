package mju.iphak.maru_egg.question.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.question.domain.Question;

@Builder
@Schema(description = "질문 응답 DTO", example = """
	{
	  "id": 600697396846981500,
	  "content": "수시 일정 알려주세요."
	  "dateInformation": "생성일자: 2024-07-15T23:36:59.834804, 마지막 DB 갱신일자: 2024-07-15T23:36:59.834804",
	  "answer": {
	    "id": 600697396935061600,
	    "content": "2024년 수시 일정은 다음과 같습니다:\\n\\n- 전체 전형 2024.12.19.(목)~12.26.(목) 18:00: 최초합격자 발표 및 시충원 관련 내용 공지 예정\\n- 문서등록 및 등록금 납부 기간: 2025. 2. 10.(월) 10:00 ~ 2. 12.(수) 15:00\\n- 등록금 납부 기간: 2024.12.16.(월) 10:00 ~ 12. 18.(수) 15:00\\n\\n추가로, 복수지원 관련하여 수시모집의 모든 전형에 중복 지원이 가능하며, 최대 6개 이내의 전형에만 지원할 수 있습니다. 반드시 지정 기간 내에 문서등록과 최종 등록(등록금 납부)을 해야 합니다. 또한, 합격자는 합격한 대학에 등록하지 않을 경우 합격 포기로 간주되니 유의하시기 바랍니다.",
	    "renewalYear": 2024,
	    "dateInformation": "생성일자: 2024-07-15T23:36:59.847690, 마지막 DB 갱신일자: 2024-07-15T23:36:59.847690"
	  },
	  "references": [
	    {
		  "title": "2025학년도 수시모집",
		  "link": "https://iphak.mju.ac.kr/main/"
	    },
	    {
		  "title": "2025학년도 정시모집",
		  "link": "https://iphak.mju.ac.kr/main/"
	    }
	  ]
	}
	""")
public record QuestionResponse(
	@Schema(description = "질문 id")
	Long id,

	@Schema(description = "질문")
	String content,

	@Schema(description = "답변 DB 생성 및 업데이트 날짜")
	String dateInformation,

	@Schema(description = "답변 DTO")
	AnswerResponse answer,

	@Schema(description = "답변 DTO")
	List<AnswerReferenceResponse> references
) {

	public static QuestionResponse of(Question question, AnswerResponse answerResponse,
		List<AnswerReferenceResponse> answerReferenceResponses) {
		return QuestionResponse.builder()
			.id(question.getId())
			.content(question.getContent())
			.dateInformation(question.getDateInformation())
			.answer(answerResponse)
			.references(answerReferenceResponses)
			.build();
	}
}