package mju.iphak.maru_egg.answer.application.process;

import static mju.iphak.maru_egg.answer.utils.AnswerGuideUtils.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.application.create.CreateAnswer;
import mju.iphak.maru_egg.answer.application.rag.RagAnswer;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answerreference.application.create.BatchCreateAnswerReference;
import mju.iphak.maru_egg.question.application.create.CreateQuestionByTypeAndCategory;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ProcessAnswerService implements ProcessAnswer {

	private final RagAnswer ragAnswer;
	private final CreateQuestionByTypeAndCategory createQuestionByTypeAndCategory;
	private final CreateAnswer createAnswer;
	private final BatchCreateAnswerReference createAnswerReference;

	public QuestionResponse invoke(AdmissionType type, AdmissionCategory category, String content,
		String contentToken) {
		LLMAskQuestionRequest askQuestionRequest = getLlmAskQuestionRequest(type,
			category, content);

		LLMAnswerResponse llmAnswerResponse = ragAnswer.invoke(askQuestionRequest).block();

		if (isInvalidAnswer(llmAnswerResponse))
			QuestionResponse.valueOfInvalidQuestion(content, generateGuideAnswer(llmAnswerResponse.references()));

		Question newQuestion = createQuestionByTypeAndCategory.invoke(type,
			AdmissionCategory.convertToCategory(llmAnswerResponse.questionCategory()), content, contentToken);
		Answer answer = createAnswer.invoke(newQuestion, llmAnswerResponse.answer());
		createAnswerReference.invoke(answer, llmAnswerResponse.references());

		return getQuestionResponse(answer, newQuestion, llmAnswerResponse);
	}

	private LLMAskQuestionRequest getLlmAskQuestionRequest(final AdmissionType type,
		final AdmissionCategory category, final String content) {
		String questionCategory = category == null ? "" : category.getCategory();
		return LLMAskQuestionRequest.of(
			type.getType(),
			questionCategory,
			content
		);
	}

	private QuestionResponse getQuestionResponse(final Answer answer, final Question newQuestion,
		final LLMAnswerResponse llmAnswerResponse) {
		AnswerResponse answerResponse = AnswerResponse.from(answer);
		return QuestionResponse.of(newQuestion, answerResponse, llmAnswerResponse.references());
	}
}
