package mju.iphak.maru_egg.question.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

public interface QuestionRepositoryCustom {
	Optional<List<QuestionCore>> searchQuestionsByContentTokenAndType(final String contentToken,
		final QuestionType type);

	Optional<List<QuestionCore>> searchQuestionsByContentTokenAndTypeAndCategory(final String contentToken,
		final QuestionType type,
		final QuestionCategory category);

	SliceQuestionResponse<SearchedQuestionsResponse> searchQuestionsOfCursorPagingByContent(final String content,
		final Integer cursorViewCount, final Long questionId, final Pageable pageable);
}