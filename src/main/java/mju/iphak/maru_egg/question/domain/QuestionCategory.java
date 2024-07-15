package mju.iphak.maru_egg.question.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionCategory {
	ADMISSION_GUIDELINE("모집요강"),
	PASSING_RESULT("입시결과"),
	PAST_QUESTIONS("기출문제"),
	UNIV_LIFE("대학생활"),
	INTERVIEW_PRACTICAL_TEST("면접/실기"),
	ETC("기타");

	private final String questionCategory;

	@Override
	public String toString() {
		return this.questionCategory;
	}
}
