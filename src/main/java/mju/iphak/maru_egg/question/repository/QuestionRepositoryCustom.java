package mju.iphak.maru_egg.question.repository;

import java.util.List;
import java.util.Optional;

import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;

public interface QuestionRepositoryCustom {
	Optional<List<QuestionCore>> searchQuestionsByContentTokenAndType(String contentToken, QuestionType type);

	Optional<List<QuestionCore>> searchQuestionsByContentTokenAndTypeAndCategory(String contentToken,
		QuestionType type,
		QuestionCategory category);
}