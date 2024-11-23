package mju.iphak.maru_egg.answer.application.command.process;

import static mju.iphak.maru_egg.answer.utils.AnswerGuideUtils.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.answer.application.command.create.CreateRAGAnswer;
import mju.iphak.maru_egg.answer.application.query.rag.RagAnswer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.request.SaveRAGAnswerRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProcessAnswerService implements ProcessAnswer {

	private final RagAnswer ragAnswer;
	private final CreateRAGAnswer createRAGAnswer;

	public QuestionResponse invoke(QuestionRequest request, String contentToken) {
		LLMAskQuestionRequest askQuestionRequest = getLlmAskQuestionRequest(request);

		LLMAnswerResponse llmAnswerResponse = ragAnswer.invoke(askQuestionRequest).block();

		if (isInvalidAnswer(llmAnswerResponse)) {
			return QuestionResponse.valueOfRAG(request.content(), generateGuideAnswer(llmAnswerResponse.references()));
		}

		SaveRAGAnswerRequest saveRAGAnswerRequest = SaveRAGAnswerRequest.of(request, contentToken, llmAnswerResponse);

		createRAGAnswer.invoke(saveRAGAnswerRequest);
		return QuestionResponse.valueOfRAG(request.content(), llmAnswerResponse.answer());
	}

	private LLMAskQuestionRequest getLlmAskQuestionRequest(final QuestionRequest request) {
		String questionCategory = request.category() == null ? "" : request.category().getCategory();
		return LLMAskQuestionRequest.of(
			request.type().getType(),
			questionCategory,
			request.content()
		);
	}
}
