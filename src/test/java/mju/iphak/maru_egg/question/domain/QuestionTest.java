package mju.iphak.maru_egg.question.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuestionTest {

	private Question question;

	@BeforeEach
	void setUp() {
		question = Question.of("질문입니다.", "질문", QuestionType.SUSI, QuestionCategory.ADMISSION_GUIDELINE);
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

}