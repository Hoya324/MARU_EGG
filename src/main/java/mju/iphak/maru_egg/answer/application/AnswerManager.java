package mju.iphak.maru_egg.answer.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.domain.AnswerReference;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerReferenceRepository;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AnswerManager {

	private static final String INVALID_ANSWER_ONE = "해당 내용에 대한 정보는 존재하지 않습니다.";
	private static final String INVALID_ANSWER_TWO = "제공된 정보 내에서 답변할 수 없습니다.";
	private static final String BASE_MESSAGE = "질문해주신 내용에 대한 적절한 정보을 발견하지 못 했습니다.\n\n대신 질문해주신 내용에 가장 적합한 자료들을 골라봤어요. 참고하셔서 다시 질문해주세요!\n\n\n\n";
	private static final String REFERENCE_TEXT_AND_LINK = "**참고자료 %d : [%s [바로가기]](%s)**\n\n";
	private static final String IPHAK_OFFICE_NUMBER_GUIDE = "\n\n입학처 상담 전화번호 : 02-300-1799, 1800";
	private static final String PAGE_SPLIT_REGEX = "page=";
	private static final String PAGE_ANNOUNCE = "의 페이지 ";

	private final QuestionRepository questionRepository;
	private final AnswerApiClient answerApiClient;
	private final AnswerReferenceRepository answerReferenceRepository;
	private final AnswerRepository answerRepository;

	public QuestionResponse processNewQuestion(QuestionType type, QuestionCategory category, String content,
		String contentToken) {
		LLMAskQuestionRequest askQuestionRequest = LLMAskQuestionRequest.of(
			type.getType(),
			category == null ? "" : category.getCategory(),
			content
		);

		LLMAnswerResponse llmAnswerResponse = answerApiClient.askQuestion(askQuestionRequest).block();

		if (isInvalidAnswer(llmAnswerResponse)) {
			return QuestionResponse.valueOfInvalidQuestion(content, getGuideAnswer(llmAnswerResponse.references()));
		}

		return saveAndGetQuestionResponse(type, content, contentToken, llmAnswerResponse);
	}

	@Transactional(readOnly = true)
	public Answer getAnswerByQuestionId(Long questionId) {
		return answerRepository.findByQuestionId(questionId)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_ANSWER_BY_QUESTION_ID.getMessage(), questionId)));
	}

	public void updateAnswerContent(final Long id, final String content) {
		Answer answer = answerRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_ANSWER.getMessage(), id)));
		answer.updateContent(content);
	}

	public void createAnswer(final Question question, final CreateAnswerRequest request) {
		Answer answer = request.toEntity(question);
		answerRepository.save(answer);
	}

	private QuestionResponse saveAndGetQuestionResponse(final QuestionType type, final String content,
		final String contentToken, final LLMAnswerResponse llmAnswerResponse) {
		Question newQuestion = saveQuestion(type,
			QuestionCategory.convertToCategory(llmAnswerResponse.questionCategory()), content, contentToken);
		Answer newAnswer = saveAnswer(newQuestion, llmAnswerResponse.answer());
		saveAnswerReferences(newAnswer, llmAnswerResponse.references());

		return createQuestionResponse(newQuestion, newAnswer, llmAnswerResponse.references());
	}

	private static String getGuideAnswer(final List<AnswerReferenceResponse> references) {
		return BASE_MESSAGE + references.stream()
			.map(reference -> {
				String page = PAGE_ANNOUNCE + reference.link().split(PAGE_SPLIT_REGEX)[1];
				String title = reference.title() + page;
				return String.format(REFERENCE_TEXT_AND_LINK,
					references.indexOf(reference) + 1,
					title,
					reference.link());
			})
			.collect(Collectors.joining("\n")) + IPHAK_OFFICE_NUMBER_GUIDE;
	}

	private boolean isInvalidAnswer(LLMAnswerResponse llmAnswerResponse) {
		return llmAnswerResponse.answer() == null || llmAnswerResponse.answer().contains(INVALID_ANSWER_ONE)
			|| llmAnswerResponse.answer().contains(INVALID_ANSWER_TWO);
	}

	private Question saveQuestion(QuestionType type, QuestionCategory category, String content, String contentToken) {
		Question question = Question.of(content, contentToken, type, category);
		return questionRepository.save(question);
	}

	private void saveAnswerReferences(Answer answer, List<AnswerReferenceResponse> referenceResponses) {
		List<AnswerReference> answerReferences = referenceResponses.stream()
			.map(reference -> AnswerReference.of(reference.title(), reference.link(), answer))
			.toList();
		answerReferenceRepository.saveAll(answerReferences);
	}

	private Answer saveAnswer(Question question, String content) {
		Answer newAnswer = Answer.of(question, content);
		return answerRepository.save(newAnswer);
	}

	private QuestionResponse createQuestionResponse(Question question, Answer answer,
		List<AnswerReferenceResponse> answerReferenceResponses) {
		AnswerResponse answerResponse = AnswerResponse.from(answer);
		return QuestionResponse.of(question, answerResponse, answerReferenceResponses);
	}
}
