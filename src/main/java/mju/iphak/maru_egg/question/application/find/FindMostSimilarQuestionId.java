package mju.iphak.maru_egg.question.application.find;

import java.util.List;

import mju.iphak.maru_egg.question.dao.response.QuestionCore;

public interface FindMostSimilarQuestionId {

	Long invoke(List<QuestionCore> questionCores, String contentToken);
}
