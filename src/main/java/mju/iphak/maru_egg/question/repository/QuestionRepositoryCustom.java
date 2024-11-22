package mju.iphak.maru_egg.question.repository;

import java.util.List;
import java.util.Optional;

import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.dao.request.QuestionCoreDAO;
import mju.iphak.maru_egg.question.dao.request.SelectQuestions;
import mju.iphak.maru_egg.question.dao.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

public interface QuestionRepositoryCustom {
	Optional<List<QuestionCore>> searchQuestions(final QuestionCoreDAO questionCoreDAO);

	SliceQuestionResponse<SearchedQuestionsResponse> searchQuestionsOfCursorPaging(
		final SelectQuestions selectQuestions);
}