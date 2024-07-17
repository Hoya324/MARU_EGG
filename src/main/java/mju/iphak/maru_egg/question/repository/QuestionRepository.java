package mju.iphak.maru_egg.question.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
	List<Question> findAllByQuestionTypeAndQuestionCategory(QuestionType type, QuestionCategory category);
}