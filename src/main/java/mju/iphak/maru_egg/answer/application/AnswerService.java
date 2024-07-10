package mju.iphak.maru_egg.answer.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AnswerService {

	private final AnswerRepository answerRepository;

	public Answer getAnswerByQuestionId(Long questionId) {
		return answerRepository.findByQuestionId(questionId)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_ANSWER.getMessage(), questionId)));
	}
}
