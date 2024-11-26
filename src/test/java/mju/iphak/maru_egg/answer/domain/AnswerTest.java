package mju.iphak.maru_egg.answer.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.question.domain.Question;

class AnswerTest {

	@Mock
	private Question question;

	private Answer answer;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		answer = createAnswer("기존 답변 내용");
	}

	@DisplayName("[성공] 답변 내용을 새로운 값으로 업데이트")
	@Test
	void 답변_내용_업데이트_성공() {
		// given
		String newContent = "새로운 답변 내용";

		// when
		updateAnswerContent(newContent);

		// then
		assertEquals(newContent, answer.getContent());
	}

	@DisplayName("[실패] null 값으로 답변 내용을 업데이트하지 않음")
	@Test
	void 답변_내용_업데이트_실패_null값() {
		// given
		String originalContent = answer.getContent();

		// when
		updateAnswerContent(null);

		// then
		assertEquals(originalContent, answer.getContent());
	}

	private Answer createAnswer(String content) {
		return Answer.of(question, content);
	}

	private void updateAnswerContent(String newContent) {
		answer.updateContent(newContent);
	}
}
