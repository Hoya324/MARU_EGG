package mju.iphak.maru_egg.answer.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.answer.repository.AnswerReferenceRepository;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

public class AnswerManagerTest {

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private AnswerApiClient answerApiClient;

	@Mock
	private AnswerReferenceRepository answerReferenceRepository;

	@Mock
	private AnswerRepository answerRepository;

	@InjectMocks
	private AnswerManager answerManager;

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
			answerManager.getAnswerByQuestionId(1L);
		});

		assertEquals("질문 id가 1인 답변을 찾을 수 없습니다.", exception.getMessage());
	}

	@DisplayName("답변 생성에 성공한 경우")
	@Test
	public void 답변_생성_성공() {
		// given
		CreateAnswerRequest answerRequest = new CreateAnswerRequest("example answer content", 2024);
		CreateQuestionRequest request = new CreateQuestionRequest("example content", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE, answerRequest);
		Question question = request.toEntity();
		Answer answer = answerRequest.toEntity(question);

		when(answerRepository.save(any(Answer.class))).thenReturn(answer);

		// when
		answerManager.createAnswer(question, answerRequest);

		// then
		verify(answerRepository, times(1)).save(any(Answer.class));
	}

	@DisplayName("답변 내용 수정 실패")
	@Test
	public void 답변_내용_수정_실패_NOTFOUND() {
		// given
		Long id = 1L;
		when(answerRepository.findById(id)).thenReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			answerManager.updateAnswerContent(id, "새로운 내용");
		});

		assertThat("답변 id가 1인 답변을 찾을 수 없습니다.").isEqualTo(exception.getMessage());
	}
}
