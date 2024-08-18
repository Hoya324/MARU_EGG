package mju.iphak.maru_egg.question.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.answer.application.AnswerApiClient;
import mju.iphak.maru_egg.answer.application.AnswerManager;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.common.utils.NLP.TextSimilarityUtils;
import mju.iphak.maru_egg.common.utils.PhraseExtractionUtils;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class QuestionProcessingService {

	private static final double STANDARD_SIMILARITY = 0.95;

	private final QuestionRepository questionRepository;
	private final AnswerApiClient answerApiClient;
	private final AnswerManager answerManager;

	public QuestionResponse question(final QuestionType type, final QuestionCategory category, final String content) {
		String contentToken = PhraseExtractionUtils.extractPhrases(content);

		List<QuestionCore> questionCores = getQuestionCores(type, category, contentToken);
		if (questionCores.isEmpty()) {
			log.info("저장된 질문이 없어 새롭게 LLM서버에 질문을 요청합니다.");
			return answerManager.processNewQuestion(type, category, content, contentToken);
		}

		Long mostSimilarQuestionId = findMostSimilarQuestionId(questionCores, contentToken);
		if (mostSimilarQuestionId != null) {
			log.info("유사한 질문이 있어 DB에서 질문을 재사용합니다.");
			return getExistingQuestionResponse(mostSimilarQuestionId);
		}

		log.info("유사한 질문이 없어 새롭게 LLM서버에 질문을 요청합니다.");
		return answerManager.processNewQuestion(type, category, content, contentToken);
	}

	public QuestionResponse getQuestion(final Long questionId) {
		return getExistingQuestionResponse(questionId);
	}

	private List<QuestionCore> getQuestionCores(QuestionType type, QuestionCategory category, String contentToken) {
		return category == null ?
			questionRepository.searchQuestionsByContentTokenAndType(contentToken, type)
				.orElse(Collections.emptyList()) :
			questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(contentToken, type, category)
				.orElse(Collections.emptyList());
	}

	private QuestionResponse getExistingQuestionResponse(Long questionId) {
		Question question = getQuestionById(questionId);
		question.incrementViewCount();
		Answer answer = answerApiClient.getAnswerByQuestionId(question.getId());
		List<AnswerReferenceResponse> answerReferenceResponses = answer.getReferences().stream()
			.map(reference -> AnswerReferenceResponse.of(reference.getTitle(), reference.getLink()))
			.toList();
		return createQuestionResponse(question, answer, answerReferenceResponses);
	}

	private Question getQuestionById(Long questionId) {
		return questionRepository.findById(questionId)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_BY_ID.getMessage(), questionId)));
	}

	private QuestionResponse createQuestionResponse(Question question, Answer answer,
		List<AnswerReferenceResponse> answerReferenceResponses) {
		AnswerResponse answerResponse = AnswerResponse.from(answer);
		return QuestionResponse.of(question, answerResponse, answerReferenceResponses);
	}

	public Long findMostSimilarQuestionId(List<QuestionCore> questionCores, String contentToken) {
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
}