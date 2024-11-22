package mju.iphak.maru_egg.answer.application.create;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answerreference.application.create.BatchCreateAnswerReference;
import mju.iphak.maru_egg.answerreference.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.question.application.create.CreateQuestionByTypeAndCategory;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.request.SaveRAGAnswerRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CreateRAGAnswerService implements CreateRAGAnswer {

	private final CreateAnswer createAnswer;
	private final CreateQuestionByTypeAndCategory createQuestionByTypeAndCategory;
	private final BatchCreateAnswerReference createAnswerReference;

	public QuestionResponse invoke(SaveRAGAnswerRequest request) {
		Question newQuestion = createQuestionByTypeAndCategory.invoke(
			request.type(),
			request.category(),
			request.content(),
			request.contentToken()
		);

		Answer answer = createAnswer.invoke(newQuestion, request.answerContent());
		createAnswerReference.invoke(answer, request.references());

		return QuestionResponse.of(
			newQuestion,
			AnswerResponse.from(answer),
			request.references().stream()
				.map(reference -> AnswerReferenceResponse.of(reference.title(), reference.link()))
				.toList()
		);
	}
}
