package mju.iphak.maru_egg.question.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

	@DisplayName("질문이 확인 되었을 때 상태 변화 성공한 경우")
	@Test
	public void 질문_체크_변경_성공() {
		// given // when
		question.updateIsChecked();

		// then
		assertThat(question.isChecked()).isTrue();
	}

	@DisplayName("질문이 확인 되었을 때 상태 변화 실패한 경우")
	@Test
	public void 질문_체크_변경_실패() {
		// given // when
		question.updateIsChecked();

		// then
		assertThat(question.isChecked()).isNotEqualTo(false);
	}

	@Test
	void updateContent_새로운_content로_업데이트() {
		// given
		String newContent = "새로운 답변 내용";

		// when
		question.updateContent(newContent);

		// then
		assertEquals(newContent, question.getContent());
	}

	@Test
	void updateContent_null_content로_업데이트하지_않음() {
		// given
		String originalContent = question.getContent();

		// when
		question.updateContent(null);

		// then
		assertEquals(originalContent, question.getContent());
	}
}