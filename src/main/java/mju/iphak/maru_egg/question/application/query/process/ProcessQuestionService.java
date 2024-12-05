package mju.iphak.maru_egg.question.application.query.process;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.answer.application.command.process.ProcessAnswer;
import mju.iphak.maru_egg.common.utils.PhraseExtractionUtils;
import mju.iphak.maru_egg.question.application.query.find.FindMostSimilarQuestionId;
import mju.iphak.maru_egg.question.application.query.find.FindQuestion;
import mju.iphak.maru_egg.question.dao.request.QuestionCoreDAO;
import mju.iphak.maru_egg.question.dao.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProcessQuestionService implements ProcessQuestion {

	private final QuestionRepository questionRepository;
	private final ProcessAnswer processAnswer;
	private final FindQuestion findQuestion;
	private final FindMostSimilarQuestionId findMostSimilarQuestionId;

	public QuestionResponse invoke(final QuestionRequest request) {
		String contentToken = PhraseExtractionUtils.extractPhrases(request.content());

		QuestionCoreDAO questionCoreDAO = QuestionCoreDAO.of(request, contentToken);

		List<QuestionCore> questionCores = questionRepository.searchQuestions(questionCoreDAO)
			.orElse(Collections.emptyList());
		if (questionCores.isEmpty()) {
			return processAnswer.invoke(request, contentToken);
		}

		Long mostSimilarQuestionId = findMostSimilarQuestionId.invoke(questionCores, contentToken);
		if (mostSimilarQuestionId != null) {
			log.info("유사한 질문{ 전형 타입: \"{}\", 전형: \"{}\", 질문: \"{}\" }이 있어 DB에서 불러옵니다.", request.type(),
				request.category(), request.content());
			return findQuestion.invoke(mostSimilarQuestionId);
		}

		return processAnswer.invoke(request, contentToken);
	}
}