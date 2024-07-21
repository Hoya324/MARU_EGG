package mju.iphak.maru_egg.answer.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
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
		answer = Answer.of(question, "기존 답변 내용");
	}

	@Test
	void updateContent_새로운_content로_업데이트() {
		// given
		String newContent = "새로운 답변 내용";

		// when
		answer.updateContent(newContent);

		// then
		assertEquals(newContent, answer.getContent());
	}

	@Test
	void updateContent_null_content로_업데이트하지_않음() {
		// given
		String originalContent = answer.getContent();

		// when
		answer.updateContent(null);

		// then
		assertEquals(originalContent, answer.getContent());
	}
}