package mju.iphak.maru_egg.question.utils;

import static mju.iphak.maru_egg.question.utils.PhraseExtractionUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import mju.iphak.maru_egg.question.utils.NLP.TextSimilarityUtils;

public class TextSimilarityUtilsTest {

	private static List<String> documents;

	@BeforeAll
	public static void setUp() {
		documents = Arrays.asList(
			extractPhrases("수시 원서접수 기간 알려줘"),
			extractPhrases("수시 모집기간 알려줘"),
			extractPhrases("수시 모집 알려줘"),
			extractPhrases("수시 원서접수 기간 알려줘"),
			extractPhrases("수시 원서 접수"),
			extractPhrases("수시 원서"),
			extractPhrases("정시 원서 기간 알려줘"),
			extractPhrases("정시 원서 접수 기간이 언제야?"),
			extractPhrases("정시 원서"),
			extractPhrases("정시 원서 기간"),
			extractPhrases("입학 시험은 무엇이 필요한가요?'"),
			extractPhrases("입학 시험 필요한거 내놔")
		);
	}

	@DisplayName("유사도가 가장 높은 문장 찾기")
	@ParameterizedTest(name = "{index} => input={0}")
	@MethodSource("inputQuestions")
	public void 유사도가_가장_높은_문장_찾기(String input, boolean expectedBoolean) throws Exception {
		String inputPhrases = extractPhrases(input);
		double maxSimilarity = -1;
		String mostSimilarDocument = null;

		documents = documents.stream()
			.map(PhraseExtractionUtils::extractPhrases)
			.toList();

		for (String document : documents) {
			Map<CharSequence, Integer> tfIdf1 = TextSimilarityUtils.computeTfIdf(documents, inputPhrases);
			Map<CharSequence, Integer> tfIdf2 = TextSimilarityUtils.computeTfIdf(documents, document);

			double similarity = TextSimilarityUtils.computeCosineSimilarity(tfIdf1, tfIdf2);

			if (similarity > maxSimilarity) {
				maxSimilarity = similarity;
				mostSimilarDocument = document;
			}
		}

		System.out.println("============================================================");
		System.out.println("Input: " + extractPhrases(input));
		System.out.println("Most Similar Document: " + mostSimilarDocument);
		System.out.println("Similarity: " + maxSimilarity);
		System.out.println("============================================================\n");

		assertNotNull(mostSimilarDocument, "There should be a most similar document.");
		assertThat(expectedBoolean).isEqualTo(maxSimilarity >= 0.95);
	}

	private static Stream<Arguments> inputQuestions() {
		return Stream.of(
			Arguments.of("수시에서 모집 기간 알려주세욬ㅋㅋㅋ", true),
			Arguments.of("수시 지원 마감일은 언제인가요?", false),
			Arguments.of("수시 마감일", false),
			Arguments.of("수시 지원서 제출", false),
			Arguments.of("수시 원서 접수 기간", true),
			Arguments.of("수시 지원서 제출 기간", false),
			Arguments.of("수시 제출", false),
			Arguments.of("수시 지원서", false),
			Arguments.of("정시 원서접수 기간 알려줘", false),
			Arguments.of("정시 지원 마감일은 언제인가요?", false),
			Arguments.of("정시 마감일", false),
			Arguments.of("정시 지원서 제출", false),
			Arguments.of("정시 원서 접수 기간", false),
			Arguments.of("정시 지원서 제출 기간", false),
			Arguments.of("정시 제출", false),
			Arguments.of("정시 지원서", false),
			Arguments.of("입학 시험", true)
		);
	}
}