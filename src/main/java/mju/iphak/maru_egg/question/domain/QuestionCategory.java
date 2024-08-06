package mju.iphak.maru_egg.question.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "질문 카테고리", enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum QuestionCategory {
	@JsonProperty("ADMISSION_GUIDELINE") ADMISSION_GUIDELINE("모집요강"),
	@JsonProperty("PASSING_RESULT") PASSING_RESULT("입시결과"),
	@JsonProperty("PAST_QUESTIONS") PAST_QUESTIONS("기출문제"),
	@JsonProperty("INTERVIEW_PRACTICAL_TEST") INTERVIEW_PRACTICAL_TEST("면접/실기");

	private final String category;

	@Override
	public String toString() {
		return this.category;
	}

	public static QuestionCategory convertToCategory(String category) {
		if (category.equals(ADMISSION_GUIDELINE.getCategory())) {
			return ADMISSION_GUIDELINE;
		}

		if (category.equals(PASSING_RESULT.getCategory())) {
			return PASSING_RESULT;
		}

		if (category.equals(PAST_QUESTIONS.getCategory())) {
			return PAST_QUESTIONS;
		}

		if (category.equals(INTERVIEW_PRACTICAL_TEST.getCategory())) {
			return INTERVIEW_PRACTICAL_TEST;
		}

		return null;
	}
}
