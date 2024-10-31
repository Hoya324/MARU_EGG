package mju.iphak.maru_egg.question.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.application.AnswerManager;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.dao.request.SelectQuestions;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionListItemResponse;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class QuestionService {

	private final QuestionRepository questionRepository;
	private final AnswerManager answerManager;

	public List<QuestionListItemResponse> getQuestions(final AdmissionType type, final AdmissionCategory category) {
		List<Question> questions = findQuestions(type, category);
		return questions.stream()
			.map(question -> createQuestionResponse(question, answerManager.getAnswerByQuestionId(question.getId())))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public SliceQuestionResponse<SearchedQuestionsResponse> searchQuestionsOfCursorPaging(final AdmissionType type,
		final AdmissionCategory category, final String content, final Integer cursorViewCount, final Long questionId,
		final Integer size) {
		Pageable pageable = PageRequest.of(0, size);
		SliceQuestionResponse<SearchedQuestionsResponse> response;

		SelectQuestions selectQuestions = SelectQuestions.of(type, category, content, cursorViewCount, questionId,
			pageable);
		response = questionRepository.searchQuestionsOfCursorPaging(selectQuestions);
		return response;
	}

	public void checkQuestion(final Long id) {
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_BY_ID.getMessage(), id)));
		question.updateIsChecked();
	}

	public void createQuestion(final CreateQuestionRequest request) {
		Question question = request.toEntity();
		questionRepository.save(question);
		answerManager.createAnswer(question, request.answer());
	}

	public void deleteQuestion(final Long id) {
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_BY_ID.getMessage(), id)));
		questionRepository.delete(question);
	}

	private List<Question> findQuestions(final AdmissionType type, final AdmissionCategory category) {
		if (category == null) {
			return questionRepository.findAllByAdmissionTypeOrderByViewCountDesc(type);
		}
		return questionRepository.findAllByAdmissionTypeAndAdmissionCategoryOrderByViewCountDesc(type, category);
	}

	private QuestionListItemResponse createQuestionResponse(final Question question, final Answer answer) {
		AnswerResponse answerResponse = AnswerResponse.from(answer);
		return QuestionListItemResponse.of(question, answerResponse);
	}

	public void updateQuestionContent(final Long id, final String content) {
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_BY_ID.getMessage(), id)));
		question.updateContent(content);
	}
}