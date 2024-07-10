package mju.iphak.maru_egg.answer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mju.iphak.maru_egg.answer.domain.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
	Optional<Answer> findByQuestionId(Long id);
}
