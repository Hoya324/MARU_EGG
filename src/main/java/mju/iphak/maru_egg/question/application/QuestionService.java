package mju.iphak.maru_egg.question.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.answer.application.AnswerService;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QuestionService {

	private final QuestionRepository questionRepository;
	private final AnswerService answerService;

	// TODO: 질문이 이미 존재하는지 확인 (유사도 검사)
	public QuestionResponse getQuestionResponse(final QuestionType type, final QuestionCategory category,
		final String content) {

		Question question = questionRepository.findByContentAndQuestionCategoryAndQuestionType(
				content, category, type)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION.getMessage(), type, category, content)));

		Answer answer = answerService.getAnswerByQuestionId(question.getId());

		AnswerResponse answerResponse = AnswerResponse.from(answer);
		return QuestionResponse.of(question, answerResponse);
	}
}
