package mju.iphak.maru_egg.question.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	Optional<Question> findByContentAndQuestionCategoryAndQuestionType(final String content,
		final QuestionCategory questionCategory,
		final QuestionType questionType);
}
