package mju.iphak.maru_egg.question.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.answer.application.AnswerService;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.domain.AnswerReference;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerReferenceRepository;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;
import mju.iphak.maru_egg.question.utils.NLP.TextSimilarityUtils;
import mju.iphak.maru_egg.question.utils.PhraseExtractionUtils;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class QuestionProcessingService {

	private static final double STANDARD_SIMILARITY = 0.95;
	private static final String UNCLASSIFIED = "";

	private final QuestionRepository questionRepository;
	private final AnswerService answerService;
	private final AnswerReferenceRepository answerReferenceRepository;

	public QuestionResponse question(final QuestionType type, final QuestionCategory category, final String content) {
		String contentToken = PhraseExtractionUtils.extractPhrases(content);
		List<QuestionCore> questionCores = getQuestionCores(type, category, contentToken);

		if (questionCores.isEmpty()) {
			log.info("저장된 질문이 없어 새롭게 LLM서버에 질문을 요청합니다.");
			return askNewQuestion(type, category, content, contentToken);
		}

		Long mostSimilarQuestionId = getMostSimilarQuestionId(questionCores, contentToken);

		if (mostSimilarQuestionId != null) {
			log.info("유사한 질문이 있어 DB에서 질문을 재사용합니다.");
			return getExistingQuestionResponse(mostSimilarQuestionId);
		}

		log.info("유사한 질문이 없어 새롭게 LLM서버에 질문을 요청합니다.");
		return askNewQuestion(type, category, content, contentToken);
	}

	private QuestionResponse askNewQuestion(QuestionType type, QuestionCategory category, String content,
		String contentToken) {
		LLMAskQuestionRequest askQuestionRequest = LLMAskQuestionRequest.of(type.getType(),
			isCategoryNullThen(category),
			content);

		LLMAnswerResponse llmAnswerResponse = answerService.askQuestion(askQuestionRequest).block();
		Question newQuestion = saveQuestion(type,
			QuestionCategory.convertToCategory(llmAnswerResponse.questionCategory()), content, contentToken);
		Answer newAnswer = saveAnswer(newQuestion, llmAnswerResponse.answer());
		saveAnswerReference(newAnswer, llmAnswerResponse.references());
		return createQuestionResponse(newQuestion, newAnswer, llmAnswerResponse.references());
	}

	private void saveAnswerReference(final Answer answer,
		List<AnswerReferenceResponse> referenceResponses) {
		List<AnswerReference> answerReferences = referenceResponses.stream()
			.map((reference) -> AnswerReference.of(reference.title(), reference.link(), answer))
			.toList();
		answerReferenceRepository.saveAll(answerReferences);
	}

	private static String isCategoryNullThen(final QuestionCategory category) {
		if (category == null) {
			return UNCLASSIFIED;
		}
		return category.getCategory();
	}

	private Answer saveAnswer(final Question question, final String content) {
		Answer newAnswer = Answer.of(question, content);
		return answerService.saveAnswer(newAnswer);
	}

	private Question saveQuestion(final QuestionType type, final QuestionCategory category, final String content,
		final String contentToken) {
		Question question = Question.of(content, contentToken, type, category);
		return questionRepository.save(question);
	}

	private QuestionResponse createQuestionResponse(final Question question, final Answer answer,
		final List<AnswerReferenceResponse> answerReferenceResponses) {
		AnswerResponse answerResponse = AnswerResponse.from(answer);
		return QuestionResponse.of(question, answerResponse, answerReferenceResponses);
	}

	private Question getQuestionById(final Long questionId) {
		return questionRepository.findById(questionId)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_BY_ID.getMessage(), questionId)));
	}

	private Long getMostSimilarQuestionId(final List<QuestionCore> questionCores, final String contentToken) {
		double maxSimilarity = -1;
		Long mostSimilarContentId = null;
		List<String> contentTokens = questionCores.stream()
			.map(QuestionCore::contentToken)
			.collect(Collectors.toList());
		Map<CharSequence, Integer> tfIdf1 = computeTfIdf(contentTokens, contentToken);

		for (QuestionCore questionCore : questionCores) {
			Map<CharSequence, Integer> tfIdf2 = computeTfIdf(contentTokens, questionCore.contentToken());

			double similarity = TextSimilarityUtils.computeCosineSimilarity(tfIdf1, tfIdf2);

			if (similarity > maxSimilarity) {
				maxSimilarity = similarity;
				mostSimilarContentId = questionCore.id();
			}
		}
		if (maxSimilarity > STANDARD_SIMILARITY) {
			return mostSimilarContentId;
		}
		return null;
	}

	private Map<CharSequence, Integer> computeTfIdf(List<String> contentTokens, String contentToken) {
		try {
			return TextSimilarityUtils.computeTfIdf(contentTokens, contentToken);
		} catch (Exception e) {
			throw new RuntimeException(
				String.format(INTERNAL_ERROR_SIMILARITY.getMessage(), contentToken, contentToken));
		}
	}

	private List<QuestionCore> getQuestionCores(final QuestionType type, final QuestionCategory category,
		final String contentToken) {
		if (category == null) {
			return questionRepository.searchQuestionsByContentTokenAndType(contentToken, type)
				.orElse(Collections.emptyList());
		}
		return questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(contentToken, type, category)
			.orElse(Collections.emptyList());
	}

	private QuestionResponse getExistingQuestionResponse(Long questionId) {
		Question question = getQuestionById(questionId);
		question.incrementViewCount();
		Answer answer = answerService.getAnswerByQuestionId(question.getId());
		List<AnswerReferenceResponse> answerReferenceResponses = answer.getReferences().stream()
			.map((answerReference) -> AnswerReferenceResponse.of(answerReference.getTitle(), answerReference.getLink()))
			.toList();
		return createQuestionResponse(question, answer, answerReferenceResponses);
	}
}