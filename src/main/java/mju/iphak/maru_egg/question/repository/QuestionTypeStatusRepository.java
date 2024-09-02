package mju.iphak.maru_egg.question.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.domain.QuestionTypeStatus;

public interface QuestionTypeStatusRepository extends JpaRepository<QuestionTypeStatus, Long> {
	Optional<QuestionTypeStatus> findByQuestionType(QuestionType type);

	void deleteByQuestionType(final QuestionType questionType);
}
