package mju.iphak.maru_egg.answer.application.query.find;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class FindAnswerByQuestionIdService implements FindAnswerByQuestionId {

	private final AnswerRepository answerRepository;

	public Answer invoke(Long questionId) {
		return answerRepository.findByQuestionId(questionId)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_ANSWER_BY_QUESTION_ID.getMessage(), questionId)));
	}
}
