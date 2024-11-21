package mju.iphak.maru_egg.answer.application.create;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;

class CreateAnswerServiceTest extends MockTest {

	@Mock
	private AnswerRepository answerRepository;

	@InjectMocks
	private CreateCustomAnswerService createAnswer;

	private Question question;
	private Answer answer;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		question = mock(Question.class);
		answer = mock(Answer.class);
	}

	@DisplayName("답변 생성에 성공한 경우")
	@Test
	public void 답변_생성_성공() {
		// given
		CreateAnswerRequest answerRequest = new CreateAnswerRequest("example answer content", 2024);
		CreateQuestionRequest request = new CreateQuestionRequest("example content", AdmissionType.SUSI,
			AdmissionCategory.ADMISSION_GUIDELINE, answerRequest);
		Question question = request.toEntity();
		Answer answer = answerRequest.toEntity(question);

		when(answerRepository.save(any(Answer.class))).thenReturn(answer);

		// when
		createAnswer.invoke(question, answerRequest);

		// then
		verify(answerRepository, times(1)).save(any(Answer.class));
	}

}