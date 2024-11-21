package mju.iphak.maru_egg.answer.application.find;

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
import mju.iphak.maru_egg.question.repository.QuestionRepository;

class FindAnswerByQuestionIdServiceTest extends MockTest {

	@Mock
	private AnswerRepository answerRepository;

	@Mock
	private QuestionRepository questionRepository;

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

	@DisplayName("questionId로 답변 조회에 실패한 경우.")
	@Test
	public void 답변_조회_실패_NOTFOUND() {
		// given
		when(answerRepository.findByQuestionId(1L)).thenReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			findAnswerByQuestionId.invoke(1L);
		});

		assertEquals("질문 id가 1인 답변을 찾을 수 없습니다.", exception.getMessage());
	}

	@DisplayName("질문 조회 시 존재하지 않는 질문 ID로 인한 예외 처리")
	@Test
	void 질문_ID로_조회_시_존재하지_않는_질문_ID() {
		// given
		Long invalidQuestionId = 999L;
		when(questionRepository.findById(invalidQuestionId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(EntityNotFoundException.class, () -> {
			findAnswerByQuestionId.invoke(invalidQuestionId);
		});
	}
}