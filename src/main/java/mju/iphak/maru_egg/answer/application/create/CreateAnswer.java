package mju.iphak.maru_egg.answer.application.create;

import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.question.domain.Question;

public interface CreateAnswer {

	Answer invoke(final Question question, final String content);
}
