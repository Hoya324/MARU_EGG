package mju.iphak.maru_egg.question.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PhraseExtractionUtilsTest {

	@Test
	public void testExtractPhrases() {
		// given
		String text = "면접평가는 언제 진행되나요?";

		// when
		String result = PhraseExtractionUtils.extractPhrases(text);

		// then
		assertEquals("면접 평가 언제 진행", result);
	}
}