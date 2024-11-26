package mju.iphak.maru_egg.answer.repository;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.common.RepositoryTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

class AnswerRepositoryTest extends RepositoryTest {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@BeforeEach
	public void setUp() throws Exception {
		Question question = Question.of("테스트 질문입니다.", "테스트 질문", AdmissionType.JEONGSI,
			AdmissionCategory.ADMISSION_GUIDELINE);
		questionRepository.save(question);
		Answer answer = Answer.of(question, "테스트 답변입니다.");
		answerRepository.save(answer);
	}

	@DisplayName("[실패] 답변 조회 실패 - questionId를 찾을 수 없는 경우")
	@Test
	void 답변_조회_실패() {
		// given
		Long invalidQuestionId = 100000000000L;

		// when
		Optional<Answer> answerOptional = answerRepository.findByQuestionId(invalidQuestionId);

		// then
		assertThat(answerOptional).isEmpty();
	}

	@DisplayName("[실패] 답변 조회 실패 - questionId를 찾을 수 없는 경우 예외 메시지 확인")
	@Test
	void 답변_조회_실패_NotFound() {
		// given
		Long invalidQuestionId = 100000000000L;

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			answerRepository.findByQuestionId(invalidQuestionId)
				.orElseThrow(() -> new EntityNotFoundException(
					String.format(NOT_FOUND_ANSWER.getMessage(), invalidQuestionId)));
		});

		assertThat(exception.getMessage()).isEqualTo(
			String.format(NOT_FOUND_ANSWER.getMessage(), invalidQuestionId));
	}
}