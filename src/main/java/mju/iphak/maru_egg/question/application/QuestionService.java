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
import mju.iphak.maru_egg.answer.application.AnswerService;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCoreDTO;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;
import mju.iphak.maru_egg.question.utils.NLP.TextSimilarityUtils;
import mju.iphak.maru_egg.question.utils.PhraseExtractionUtils;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QuestionService {

	private static final double STANDARD_SIMILARITY = 0.95;

	private final QuestionRepository questionRepository;
	private final AnswerService answerService;

	@Transactional
	public QuestionResponse question(final QuestionType type, final QuestionCategory category, final String content) {
		String contentToken = PhraseExtractionUtils.extractPhrases(content);
		List<QuestionCoreDTO> questionCores = getQuestionCores(type, category, contentToken);

		if (questionCores.isEmpty()) {
			return askNewQuestion(type, category, content, contentToken);
		}

		Long mostSimilarQuestionId = getMostSimilarQuestionId(questionCores, contentToken);

		if (mostSimilarQuestionId != null) {
			return getExistingQuestionResponse(mostSimilarQuestionId);
		}

		return askNewQuestion(type, category, content, contentToken);
	}

	private QuestionResponse askNewQuestion(QuestionType type, QuestionCategory category, String content,
		String contentToken) {
		LLMAskQuestionRequest askQuestionRequest = LLMAskQuestionRequest.of(type.toString(), category.toString(),
			content);

		LLMAnswerResponse llmAnswerResponse = answerService.askQuestion(askQuestionRequest).block();

		Question newQuestion = saveQuestion(type, category, content, contentToken);
		Answer newAnswer = saveAnswer(newQuestion, llmAnswerResponse.answer());
		return createQuestionResponse(newQuestion, newAnswer);
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

	private QuestionResponse createQuestionResponse(final Question question, final Answer answer) {
		AnswerResponse answerResponse = AnswerResponse.from(answer);
		return QuestionResponse.of(question, answerResponse);
	}

	private Question getQuestionById(final Long questionId) {
		return questionRepository.findById(questionId)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_BY_ID.getMessage(), questionId)));
	}

	private Long getMostSimilarQuestionId(final List<QuestionCoreDTO> questionCores, final String contentToken) {
		double maxSimilarity = -1;
		Long mostSimilarContentId = null;
		List<String> contentTokens = questionCores.stream()
			.map(QuestionCoreDTO::contentToken)
			.collect(Collectors.toList());
		Map<CharSequence, Integer> tfIdf1 = computeTfIdf(contentTokens, contentToken);

		for (QuestionCoreDTO questionCore : questionCores) {
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

	private List<QuestionCoreDTO> getQuestionCores(final QuestionType type, final QuestionCategory category,
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
		return createQuestionResponse(question, answer);
	}
}