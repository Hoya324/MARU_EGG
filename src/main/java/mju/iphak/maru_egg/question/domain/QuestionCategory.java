package mju.iphak.maru_egg.question.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "질문 카테고리", enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum QuestionCategory {
	ADMISSION_GUIDELINE("모집요강"),
	PASSING_RESULT("입시결과"),
	PAST_QUESTIONS("기출문제"),
	UNIV_LIFE("대학생활"),
	INTERVIEW_PRACTICAL_TEST("면접/실기"),
	UNCLASSIFIED("미분류");

	private final String questionCategory;

	@Override
	public String toString() {
		return this.questionCategory;
	}
}
