package mju.iphak.maru_egg.question.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;

class QuestionTest {

	private Question question;

	@BeforeEach
	void setUp() {
		question = Question.of("질문입니다.", "질문", AdmissionType.SUSI, AdmissionCategory.ADMISSION_GUIDELINE);
	}

	@DisplayName("[성공] 질문 확인 상태를 변경")
	@Test
	void 질문_상태_변경_성공() {
		// when
		question.updateIsChecked();

		// then
		verifyCheckedState(true);
	}

	@DisplayName("[실패] 질문 확인 상태 변경 후 상태가 false가 아님")
	@Test
	void 질문_상태_변경_실패() {
		// when
		question.updateIsChecked();

		// then
		verifyCheckedStateNot(false);
	}

	@DisplayName("[성공] 질문 내용을 새 내용으로 업데이트")
	@Test
	void 질문_내용_업데이트_성공() {
		// given
		String newContent = "새로운 답변 내용";

		// when
		question.updateContent(newContent);

		// then
		verifyContent(newContent);
	}

	@DisplayName("[실패] null 값으로 질문 내용을 업데이트하지 않음")
	@Test
	void 질문_내용_업데이트_실패_null() {
		// given
		String originalContent = question.getContent();

		// when
		question.updateContent(null);

		// then
		verifyContent(originalContent);
	}

	// 공통 검증 메서드
	private void verifyCheckedState(boolean expected) {
		assertThat(question.isChecked()).isEqualTo(expected);
	}

	private void verifyCheckedStateNot(boolean unexpected) {
		assertThat(question.isChecked()).isNotEqualTo(unexpected);
	}

	private void verifyContent(String expectedContent) {
		assertThat(question.getContent()).isEqualTo(expectedContent);
	}
}
