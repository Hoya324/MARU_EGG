package mju.iphak.maru_egg.answer.application.find;

import mju.iphak.maru_egg.answer.domain.Answer;

public interface FindAnswerByQuestionId {

	Answer invoke(Long questionId);
}
