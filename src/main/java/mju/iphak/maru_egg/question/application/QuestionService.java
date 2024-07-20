package mju.iphak.maru_egg.question.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.answer.application.AnswerService;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QuestionService {

	private final QuestionRepository questionRepository;
	private final AnswerService answerService;

	public List<QuestionResponse> getQuestions(final QuestionType type, final QuestionCategory category) {
		List<Question> questions = findQuestions(type, category);
		return questions.stream()
			.map(question -> createQuestionResponse(question, answerService.getAnswerByQuestionId(question.getId())))
			.collect(Collectors.toList());
	}

	public SliceQuestionResponse<SearchedQuestionsResponse> searchQuestionsOfCursorPaging(final String content,
		final Integer cursorViewCount, final Long questionId, final Integer size) {
		Pageable pageable = PageRequest.of(0, size);
		return questionRepository.searchQuestionsOfCursorPagingByContent(
			content,
			cursorViewCount, questionId, pageable);
	}

	private List<Question> findQuestions(final QuestionType type, final QuestionCategory category) {
		if (category == null) {
			return questionRepository.findAllByQuestionType(type);
		}
		return questionRepository.findAllByQuestionTypeAndQuestionCategory(type, category);
	}

	private QuestionResponse createQuestionResponse(final Question question, final Answer answer) {
		AnswerResponse answerResponse = AnswerResponse.from(answer);
		return QuestionResponse.of(question, answerResponse);
	}
}