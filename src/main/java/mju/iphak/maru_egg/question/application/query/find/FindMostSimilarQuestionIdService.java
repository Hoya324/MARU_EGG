package mju.iphak.maru_egg.question.application.query.find;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.utils.NLP.TextSimilarityUtils;
import mju.iphak.maru_egg.question.dao.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.response.SimilarityResult;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindMostSimilarQuestionIdService implements FindMostSimilarQuestionId {

	private static final double STANDARD_SIMILARITY = 0.95;

	public Long invoke(List<QuestionCore> questionCores, String contentToken) {
		Map<CharSequence, Integer> inputQuestionTfIdf = computeTfIdf(questionCores, contentToken);

		return questionCores.stream()
			.map(core -> {
				Map<CharSequence, Integer> coreQuestionTfIdf = computeTfIdf(questionCores, core.contentToken());
				double similarity = TextSimilarityUtils.computeCosineSimilarity(inputQuestionTfIdf, coreQuestionTfIdf);
				return new SimilarityResult(core.id(), similarity);
			})
			.max(Comparator.comparingDouble(SimilarityResult::similarity))
			.filter(result -> result.similarity() > STANDARD_SIMILARITY)
			.map(SimilarityResult::id)
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
}
