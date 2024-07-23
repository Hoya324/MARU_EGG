package mju.iphak.maru_egg.answer.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Builder;
import mju.iphak.maru_egg.answer.domain.Answer;

public record LLMAnswerResponse(
	String questionType,
	String questionCategory,
	String answer,
	List<AnswerReferenceResponse> references
) {
	@Builder
	public LLMAnswerResponse {
		if (Objects.isNull(references)) {
			references = new ArrayList<>();
		}
	}

	public static LLMAnswerResponse of(String questionType, String questionCategory, Answer answer,
		List<AnswerReferenceResponse> references) {
		return LLMAnswerResponse.builder()
			.questionType(questionType)
			.questionCategory(questionCategory)
			.answer(answer.getContent())
			.references(references)
			.build();
	}
}