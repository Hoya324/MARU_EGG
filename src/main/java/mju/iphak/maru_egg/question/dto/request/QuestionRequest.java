package mju.iphak.maru_egg.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

@Schema(description = "질문 생성 요청 DTO", example = """
	{
	  "type": "SUSI 또는 PYEONIP 또는 JEONGSI 또는 JAEOEGUGMIN",
	  "category": "PAST_QUESTIONS 또는 UNIV_LIFE 또는 INTERVIEW_PRACTICAL_TEST 또는 PASSING_RESULT 또는 ADMISSION_GUIDELINE 또는 UNCLASSIFIED",
	  "content": "수시 입학 요강에 대해 알려주세요."
	}
	""")
public record QuestionRequest(

	@Schema(description = "질문 타입(수시, 정시, 편입학, 재외국민)")
	@NotNull(message = "질문 타입은 비어있을 수 없습니다.")
	QuestionType type,

	@Schema(description = "질문 카테고리(모집요강, 입시결과, 기출 문제)", nullable = true)
	QuestionCategory category,

	@Schema(description = "질문 내용")
	@NotBlank(message = "질문은 비어있을 수 없습니다.")
	String content
) {
}