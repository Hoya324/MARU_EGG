package mju.iphak.maru_egg.question.application.query.find;

import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

public interface FindQuestion {

	QuestionResponse invoke(Long questionId);
}
