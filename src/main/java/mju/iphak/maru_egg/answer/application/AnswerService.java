package mju.iphak.maru_egg.answer.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Transactional
public class AnswerService {

	private final AnswerRepository answerRepository;
	private final WebClient webClient;

	@Transactional(readOnly = true)
	public Answer getAnswerByQuestionId(Long questionId) {
		return answerRepository.findByQuestionId(questionId)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_ANSWER.getMessage(), questionId)));
	}

	public Mono<LLMAnswerResponse> askQuestion(LLMAskQuestionRequest request) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("questionType", request.questionType());
		formData.add("questionCategory", request.questionCategory());
		formData.add("question", request.question());
		return webClient.post()
			.uri("/maruegg/ask_question_api/")
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData(formData))
			.retrieve()
			.bodyToMono(LLMAnswerResponse.class);
	}

	public Answer saveAnswer(Answer answer) {
		return answerRepository.save(answer);
	}
}
