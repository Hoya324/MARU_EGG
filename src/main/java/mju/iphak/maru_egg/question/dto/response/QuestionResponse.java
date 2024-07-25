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
	  "id": 603937252297932981,
	  "content": "수시 일정 알려주세요.",
	  "dateInformation": "생성일자: 2024-07-24T22:11:02, 마지막 DB 갱신일자: 2024-07-24T22:45:25.037855",
	  "answer": {
	    "id": 603937252394403841,
	    "content": "명지대학교의 2025학년도 신입학 수시 모집 요강은 다음과 같습니다:\\n\\n1. **블라인드 면접 관련 사항**:\\n   - 명지대학교는 모든 면접 전형에서 블라인드 면접을 시행합니다. 면접 시 지원자의 성명, 수험번호, 출신 고교, 부모의 직업 및 실명 등을 언급하지 않아야 하며, 지원자의 역량에 기반한 공정한 평가가 이루어집니다.\\n   - 면접 과정에서는 신분증을 준비하고 복장 규정을 준수해야 하며, 면접 장소와 시간을 사전에 확인해야 합니다.\\n   - 면접이 진행될 때는 면접위원의 질문에 차분히 응답해야 하며, 내용에는 금지된 인적 사항이 포함되어서는 안 됩니다.\\n\\n2. **고등학교 졸업 및 등록 관련 사항**:\\n   - 고등학교 졸업예정자가 해당 연도에 졸업하지 못할 경우, 입학 허가가 취소됩니다.\\n   - 학생부교과와 학생부종합 전형 등에서 최종 등록자가 입학 후 지원 자격 기준에 미달될 경우, 입학 허가가 취소될 수 있으며, 이 경우 등록금은 반환되지 않습니다.\\n\\n3. **온라인 제공 및 개인정보 활용 동의**:\\n   - 명지대학교에 지원한 학생은 학교생활기록부 온라인 제공에 동의하는 것으로 간주됩니다.\\n   - 고등학교의 학교생활기록부 온라인 제공이 불가능한 경우, 지원자는 원서접수 이후 서류 제출 기간 내에 별도로 문서 제출을 해야 합니다.\\n   - 검정고시의 성적 증명서 및 합격 증명서도 온라인 제공에 동의한 것으로 간주됩니다.\\n\\n4. **부정행위자 처리 방침**:\\n   - 지원자의 자격을 위반한 경우, 허위 작성이 발견되거나 공정한 학생 선발을 방해한 경우에는 합격이 취소되며, 제출된 서류와 관련 비용은 반환되지 않습니다.\\n   - 부정한 방법으로 합격이 취소된 경우, 향후 지원에 제약이 있을 수 있습니다.\\n\\n자세한 내용은 명지대학교 홈페이지에서 확인하시는 것이 좋습니다. 추가 문의가 필요하시면 입학처에 직접 연락해 주십시오.",
	    "renewalYear": 2024,
	    "dateInformation": "생성일자: 2024-07-24T22:11:02, 마지막 DB 갱신일자: 2024-07-24T22:11:02"
	  },
	  "references": [
	    {
	      "title": "수시_모집요강.pdf",
	      "link": "http://3.37.12.249/media/documents/수시_모집요강.pdf#page=1"
	    },
	    {
	      "title": "수시_모집요강.pdf",
	      "link": "http://3.37.12.249/media/documents/수시_모집요강.pdf#page=80"
	    },
	    {
	      "title": "수시_모집요강.pdf",
	      "link": "http://3.37.12.249/media/documents/수시_모집요강.pdf#page=23"
	    }
	  ]
	}
	""")
public record QuestionResponse(
	@Schema(description = "질문 id", type = "string", example = "603937252297932981")
	Long id,

	@Schema(description = "질문", example = "수시 일정 알려주세요.")
	String content,

	@Schema(description = "답변 DB 생성 및 업데이트 날짜", example = "생성일자: 2024-07-24T22:11:02, 마지막 DB 갱신일자: 2024-07-24T22:45:25.037855")
	String dateInformation,

	@Schema(description = "답변 DTO")
	AnswerResponse answer,

	@Schema(description = "참고 자료 리스트")
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