package mju.iphak.maru_egg.question.utils;

import static mju.iphak.maru_egg.common.utils.PhraseExtractionUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import mju.iphak.maru_egg.common.utils.PhraseExtractionUtils;

public class PhraseExtractionUtilsTest {
	@DisplayName("텍스트 추출 검사")
	@ParameterizedTest(name = "{index} => input={0}")
	@MethodSource("inputQuestions")
	public void testExtractPhrases(String input, String contentToken) {
		// given // when
		String result = PhraseExtractionUtils.extractPhrases(input);

		// then
		System.out.println("============================================================");
		System.out.println("\ninput: " + input);
		System.out.println("contentToken: " + extractPhrases(input) + "\n");
		System.out.println("============================================================\n");

		assertEquals(contentToken, result);
	}

	private static Stream<Arguments> inputQuestions() {
		return Stream.of(
			Arguments.of("수시 원서접수 기간 알려줘", "수시 원서 접수 기간"),
			Arguments.of("수시 모집기간 알려줘", "수시 모집 기간"),
			Arguments.of("수시 모집 알려줘", "수시 모집"),
			Arguments.of("수시 원서접수 기간 알려줘", "수시 원서 접수 기간"),
			Arguments.of("수시 원서 접수", "수시 원서 접수"),
			Arguments.of("수시 원서", "수시 원서"),
			Arguments.of("정시 원서 기간 알려줘", "정시 원서 기간"),
			Arguments.of("정시 원서 접수 기간이 언제야", "정시 원서 접수 기간 언제"),
			Arguments.of("정시 원서", "정시 원서"),
			Arguments.of("정시 원서 기간", "정시 원서 기간"),
			Arguments.of("입학 시험은 무엇이 필요한가요?", "입학 시험 무엇"),
			Arguments.of("입학 시험 필요한거 내놔", "입학 시험"),
			Arguments.of("수시 입학 요강에 대해 알려주세요.", "수시 입학 요강 대해"),
			Arguments.of("수시 모집 요강알려줘", "수시 모집 요강"),
			Arguments.of("모집요강에 대해 설명해줘", "모집 요강 대해 설명"),
			Arguments.of("수시 일정 설명해줘", "수시 일정 설명"),
			Arguments.of("크리스천리더전형 알려줘", "크리스천 리더 전형"),
			Arguments.of("학생부 교과 전형 알려줘", "학생 부 교과 전형"),
			Arguments.of("교과면접전형에 대해 알려줘", "교과 접전 대해"),
			Arguments.of("학생부 종합 전형에 대해 소개해줘", "학생 부 종합 전형 대해 소개"),
			Arguments.of("학생부 종합 서류전형과 학생부 종합 면접전형의 차이가 뭐야?", "학생 부 종합 서류 전형 학생 부 종합 면접 전형 차이 뭐"),
			Arguments.of("수시로 지원할때 수능 최저 있어?", "지원 때 수능 최저"),
			Arguments.of("학생부교과는 뭐야?", "학생 부교 뭐"),
			Arguments.of("교과면접에 대해 알려줘", "교과 면접 대해"),
			Arguments.of("학교장추천전형과 교과면접의 차이는 뭐야?", "학교장 추천 전형 교과 면접 뭐"),
			Arguments.of("교과면접전형 일정 알려줘", "교과 면접 전형 일정"),
			Arguments.of("교과면접 전형에 대해 알려줘", "교과 면접 전형 대해"),
			Arguments.of("학생부교과전형에 대해 알려줘", "학생 부교 전형 대해"),
			Arguments.of("학생부 교과전형에 대해 알려줘", "학생 부 교과 전형 대해"),
			Arguments.of("학교장추천전형알려줘", "학교장 추천 전형")
		);
	}
}