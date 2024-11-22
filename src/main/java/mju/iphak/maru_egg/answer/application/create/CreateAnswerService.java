package mju.iphak.maru_egg.answer.application.create;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.question.domain.Question;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CreateAnswerService implements CreateAnswer {

	private final AnswerRepository answerRepository;

	public Answer invoke(Question question, String content) {
		Answer newAnswer = Answer.of(question, content);
		return answerRepository.save(newAnswer);
	}
}
