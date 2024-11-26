package mju.iphak.maru_egg.question.application.command.update;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UpdateQuestionContentService implements UpdateQuestionContent {

	private final QuestionRepository questionRepository;

	public void invoke(final Long id, final String content) {
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_BY_ID.getMessage(), id)));
		question.updateContent(content);
	}
}
