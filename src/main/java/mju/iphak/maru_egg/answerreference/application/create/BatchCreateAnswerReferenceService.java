package mju.iphak.maru_egg.answerreference.application.create;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answerreference.domain.AnswerReference;
import mju.iphak.maru_egg.answerreference.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answerreference.repository.AnswerReferenceRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class BatchCreateAnswerReferenceService implements BatchCreateAnswerReference {

	private final AnswerReferenceRepository answerReferenceRepository;

	public void invoke(Answer answer, List<AnswerReferenceResponse> referenceResponses) {
		List<AnswerReference> answerReferences = referenceResponses.stream()
			.map(reference -> AnswerReference.of(reference.title(), reference.link(), answer))
			.toList();
		answerReferenceRepository.saveAll(answerReferences);
	}
}
