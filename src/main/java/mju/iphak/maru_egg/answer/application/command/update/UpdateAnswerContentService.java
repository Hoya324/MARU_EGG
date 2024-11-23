package mju.iphak.maru_egg.answer.application.command.update;

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
@Transactional
public class UpdateAnswerContentService implements UpdateAnswerContent {

	private final AnswerRepository answerRepository;

	public void invoke(final Long id, final String content) {
		Answer answer = answerRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_ANSWER.getMessage(), id)));
		answer.updateContent(content);
	}
}
