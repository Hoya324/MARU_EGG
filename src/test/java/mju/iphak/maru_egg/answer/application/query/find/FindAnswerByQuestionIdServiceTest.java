package mju.iphak.maru_egg.answer.application.query.find;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.Question;

class FindAnswerByQuestionIdServiceTest extends MockTest {

	@Mock
	private AnswerRepository answerRepository;

	@InjectMocks
	private FindAnswerByQuestionIdService findAnswerByQuestionId;

	private Question question;
	private Answer answer;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		question = mock(Question.class);
		answer = mock(Answer.class);
	}

	@DisplayName("[실패] questionId로 답변 조회 요청 - 답변 없음")
	@Test
	void 답변_조회_실패_답변_없음() {
		// given
		Long questionId = 1L;
		when(answerRepository.findByQuestionId(questionId)).thenReturn(Optional.empty());

		// when
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
			() -> findAnswerByQuestionId.invoke(questionId));

		// then
		assertThat(exception.getMessage()).isEqualTo(String.format("질문 id가 %d인 답변을 찾을 수 없습니다.", questionId));
	}

	@DisplayName("[실패] 질문 ID 조회 요청 - 존재하지 않는 질문 ID")
	@Test
	void 질문_ID로_조회_실패_질문_없음() {
		// given
		Long invalidQuestionId = 999L;
		when(answerRepository.findByQuestionId(invalidQuestionId)).thenReturn(Optional.empty());

		// when
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
			() -> findAnswerByQuestionId.invoke(invalidQuestionId));

		// then
		assertThat(exception.getMessage()).isEqualTo(String.format("질문 id가 %d인 답변을 찾을 수 없습니다.", invalidQuestionId));
	}
}
