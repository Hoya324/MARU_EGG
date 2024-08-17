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
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.domain.AnswerReference;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerReferenceRepository;
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
	private static final String INVALID_ANSWER = "해당 내용에 대한 정보는 존재하지 않습니다. 정확한 내용은 입학지원팀에 문의해주세요.";

	private final QuestionRepository questionRepository;
	private final AnswerApiClient answerApiClient;
	private final AnswerReferenceRepository answerReferenceRepository;

	public QuestionResponse question(final QuestionType type, final QuestionCategory category, final String content) {
		String contentToken = PhraseExtractionUtils.extractPhrases(content);
		List<QuestionCore> questionCores = getQuestionCores(type, category, contentToken);

		if (questionCores.isEmpty()) {
			log.info("저장된 질문이 없어 새롭게 LLM서버에 질문을 요청합니다.");
			return processNewQuestion(type, category, content, contentToken);
		}

		Long mostSimilarQuestionId = findMostSimilarQuestionId(questionCores, contentToken);

		if (mostSimilarQuestionId != null) {
			log.info("유사한 질문이 있어 DB에서 질문을 재사용합니다.");
			return getExistingQuestionResponse(mostSimilarQuestionId);
		}

		log.info("유사한 질문이 없어 새롭게 LLM서버에 질문을 요청합니다.");
		return processNewQuestion(type, category, content, contentToken);
	}

	private QuestionResponse processNewQuestion(QuestionType type, QuestionCategory category, String content,
		String contentToken) {
		LLMAskQuestionRequest askQuestionRequest = LLMAskQuestionRequest.of(
			type.getType(),
			category == null ? "" : category.getCategory(),
			content
		);

		LLMAnswerResponse llmAnswerResponse = answerApiClient.askQuestion(askQuestionRequest).block();

		if (!isValidAnswer(llmAnswerResponse)) {
			return QuestionResponse.valueOfInvalidQuestion(content);
		}

		Question newQuestion = saveQuestion(type,
			QuestionCategory.convertToCategory(llmAnswerResponse.questionCategory()), content, contentToken);
		Answer newAnswer = saveAnswer(newQuestion, llmAnswerResponse.answer());
		saveAnswerReferences(newAnswer, llmAnswerResponse.references());

		return createQuestionResponse(newQuestion, newAnswer, llmAnswerResponse.references());
	}

	private boolean isValidAnswer(LLMAnswerResponse llmAnswerResponse) {
		return !llmAnswerResponse.answer().equals(INVALID_ANSWER);
	}

	private void saveAnswerReferences(Answer answer, List<AnswerReferenceResponse> referenceResponses) {
		List<AnswerReference> answerReferences = referenceResponses.stream()
			.map(reference -> AnswerReference.of(reference.title(), reference.link(), answer))
			.toList();
		answerReferenceRepository.saveAll(answerReferences);
	}

	private Answer saveAnswer(Question question, String content) {
		Answer newAnswer = Answer.of(question, content);
		return answerApiClient.saveAnswer(newAnswer);
	}

	private Question saveQuestion(QuestionType type, QuestionCategory category, String content, String contentToken) {
		Question question = Question.of(content, contentToken, type, category);
		return questionRepository.save(question);
	}

	private Long findMostSimilarQuestionId(List<QuestionCore> questionCores, String contentToken) {
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
}