package mju.iphak.maru_egg.question.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.question.domain.Question;

@Builder
@Schema(description = "질문 응답 DTO", example = """
	[
	   {
	     "id": 603841250678468600,
	     "content": "수시 입학 요강 알려주세요.",
	     "viewCount": 2,
	     "isChecked": false,
	     "dateInformation": "생성일자: 2024-07-24T15:49:33, 마지막 DB 갱신일자: 2024-07-24T15:52:49",
	     "answer": {
	       "id": 603841250942708400,
	       "content": "명지대학교의 2025학년도 신입학 수시 모집 요강은 다음과 같습니다:\\n\\n### 1. 지원자격\\n- 고등학교 졸업예정자는 당해 연도에 졸업하지 못할 경우 입학 허가가 취소됩니다.\\n- 특정 전형(학생부교과, 학생부종합 등)에 대한 지원 자격이 있으며, 최종 등록자가 자격 기준에 미달될 경우 입학 허가가 취소될 수 있습니다.\\n\\n### 2. 서류 제출 및 개인정보 활용 동의\\n- 지원자는 학교생활기록부 및 검정고시 성적증명서의 온라인 제공에 동의한 것으로 간주됩니다.\\n- 온라인 제공이 불가능한 경우, 원서접수 완료 후 서류제출 기간 내에 해당 서류를 우편으로 제출해야 합니다.\\n\\n### 3. 부정행위자 처리 방침\\n- 지원 자격을 위반한 경우나 부정한 방법으로 합격한 경우, 합격이 취소되며 제출한 서류와 등록금은 반환되지 않습니다.\\n\\n### 4. 특이자 추가서류 (농어촌학생)\\n- 부모 이혼 또는 부모 사망 시 제출해야 할 기본증명서 및 혼인관계증명서 등의 서류를 요구합니다.\\n- 모든 서류는 원본 제출을 원칙으로 하며, 사본에는 반드시 원본 대조필이 필요합니다.\\n\\n### 5. 제출기한 및 방법\\n- 제출기한: 2025년 2월 21일(금)까지 (마감일 우체국 소인분까지 인정)\\n- 제출방법: 등기우편 (방문 제출 불가)\\n- 제출주소: 서울특별시 서대문구 거북골로 34 명지대학교 입학처 인재발굴팀 (행정동 2층)\\n\\n각 전형 및 진행 절차에 대한 보다 구체적인 사항은 명지대학교 입학처의 공식 홈페이지를 참고하시기 바랍니다.",
	       "renewalYear": 2024,
	       "dateInformation": "생성일자: 2024-07-24T15:49:33, 마지막 DB 갱신일자: 2024-07-24T15:49:33"
	     }
	   }
	 ]
	""")
public record QuestionListItemResponse(
	@Schema(description = "질문 id")
	Long id,

	@Schema(description = "질문")
	String content,

	@Schema(description = "질문 조회수")
	int viewCount,

	@Schema(description = "확인된 질문-답변인지 checked (true면 확인 완료, false면 확인 미완료)")
	boolean isChecked,

	@Schema(description = "답변 DB 생성 및 업데이트 날짜")
	String dateInformation,

	@Schema(description = "답변 DTO")
	AnswerResponse answer
) {

	public static QuestionListItemResponse of(Question question, AnswerResponse answer) {
		return QuestionListItemResponse.builder()
			.id(question.getId())
			.content(question.getContent())
			.viewCount(question.getViewCount())
			.isChecked(question.isChecked())
			.dateInformation(question.getDateInformation())
			.answer(answer)
			.build();
	}
}