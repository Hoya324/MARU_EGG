package mju.iphak.maru_egg.answer.application.process;

import static mju.iphak.maru_egg.answer.utils.AnswerGuideUtils.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.application.create.CreateRAGAnswer;
import mju.iphak.maru_egg.answer.application.rag.RagAnswer;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.request.SaveRAGAnswerRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProcessAnswerService implements ProcessAnswer {

	private final RagAnswer ragAnswer;
	private final CreateRAGAnswer createRAGAnswer;

	public QuestionResponse invoke(AdmissionType type, AdmissionCategory category, String content,
		String contentToken) {
		LLMAskQuestionRequest askQuestionRequest = getLlmAskQuestionRequest(type,
			category, content);

		LLMAnswerResponse llmAnswerResponse = ragAnswer.invoke(askQuestionRequest).block();

		if (isInvalidAnswer(llmAnswerResponse)) {
			QuestionResponse.valueOfInvalidQuestion(content, generateGuideAnswer(llmAnswerResponse.references()));
		}

		SaveRAGAnswerRequest saveRAGAnswerRequest = SaveRAGAnswerRequest.builder()
			.type(type)
			.category(category)
			.content(content)
			.contentToken(contentToken)
			.answerContent(llmAnswerResponse.answer())
			.references(llmAnswerResponse.references())
			.build();

		createRAGAnswer.invoke(saveRAGAnswerRequest);
		return QuestionResponse.valueOfRAG(request.content(), llmAnswerResponse.answer());
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
