package mju.iphak.maru_egg.question.application.command.delete;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteQuestionService implements DeleteQuestion {

	private final QuestionRepository questionRepository;

	public void invoke(final Long id) {
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_BY_ID.getMessage(), id)));
		questionRepository.delete(question);
	}
}
