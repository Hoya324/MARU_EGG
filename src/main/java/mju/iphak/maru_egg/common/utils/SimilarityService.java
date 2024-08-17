package mju.iphak.maru_egg.common.utils;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.utils.NLP.TextSimilarityUtils;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@RequiredArgsConstructor
@Service
public class SimilarityService {

	private static final double STANDARD_SIMILARITY = 0.95;
	private final QuestionRepository questionRepository;

	public Long findMostSimilarQuestionId(QuestionType type, QuestionCategory category, String contentToken) {
		List<QuestionCore> questionCores = getQuestionCores(type, category, contentToken);
		Map<CharSequence, Integer> tfIdf1 = computeTfIdf(questionCores, contentToken);

		return questionCores.stream()
			.map(core -> {
				Map<CharSequence, Integer> tfIdf2 = computeTfIdf(questionCores, core.contentToken());
				double similarity = TextSimilarityUtils.computeCosineSimilarity(tfIdf1, tfIdf2);
				return similarity > STANDARD_SIMILARITY ? core.id() : null;
			})
			.filter(id -> id != null)
			.findFirst()
			.orElse(null);
	}

	private Map<CharSequence, Integer> computeTfIdf(List<QuestionCore> questionCores, String contentToken) {
		List<String> contentTokens = questionCores.stream()
			.map(QuestionCore::contentToken)
			.toList();

		try {
			return TextSimilarityUtils.computeTfIdf(contentTokens, contentToken);
		} catch (Exception e) {
			throw new RuntimeException(String.format(INTERNAL_ERROR_TEXT_SIMILARITY.getMessage()));
		}
	}

	private List<QuestionCore> getQuestionCores(QuestionType type, QuestionCategory category, String contentToken) {
		return category == null ?
			questionRepository.searchQuestionsByContentTokenAndType(contentToken, type)
				.orElse(Collections.emptyList()) :
			questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(contentToken, type, category)
				.orElse(Collections.emptyList());
	}
}
