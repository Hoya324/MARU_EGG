package mju.iphak.maru_egg.question.application.create;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateQuestionByTypeAndCategoryService implements CreateQuestionByTypeAndCategory {

	private final QuestionRepository questionRepository;

	public Question invoke(AdmissionType type, AdmissionCategory category, String content, String contentToken) {
		Question question = Question.of(content, contentToken, type, category);
		return questionRepository.save(question);
	}
}
