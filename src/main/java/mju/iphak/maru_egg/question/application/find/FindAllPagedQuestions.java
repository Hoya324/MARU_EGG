package mju.iphak.maru_egg.question.application.find;

import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.dto.request.SearchQuestionsRequest;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

public interface FindAllPagedQuestions {

	SliceQuestionResponse<SearchedQuestionsResponse> invoke(final SearchQuestionsRequest request);
}
