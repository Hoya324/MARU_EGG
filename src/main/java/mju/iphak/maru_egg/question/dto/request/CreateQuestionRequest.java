package mju.iphak.maru_egg.question.dto.request;

import static mju.iphak.maru_egg.common.utils.PhraseExtractionUtils.*;

import io.swagger.v3.oas.annotations.media.Schema;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

@Schema(description = "질문 생성 요청 DTO")
public record CreateQuestionRequest(
	@Schema(description = "질문 내용", example = "예시 질문입니다.")
	String content,

	@Schema(description = "질문 타입(수시, 정시, 편입학)", allowableValues = {"SUSI", "JEONGSI", "PYEONIP"})
	QuestionType questionType,

	@Schema(description = "질문 내용", allowableValues = {"ADMISSION_GUIDELINE", "PASSING_RESULT",
		"PAST_QUESTIONS", "INTERVIEW_PRACTICAL_TEST"})
	QuestionCategory questionCategory,

	CreateAnswerRequest answer
) {
	public Question toEntity() {
		return Question.create(content, extractPhrases(content), questionType, questionCategory);
	}
}
