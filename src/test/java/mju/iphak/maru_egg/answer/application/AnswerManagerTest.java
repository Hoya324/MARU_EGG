package mju.iphak.maru_egg.answer.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import reactor.core.publisher.Mono;

public class AnswerManagerTest extends MockTest {

	@Mock
	private AnswerRepository answerRepository;

	@Mock
	private AnswerApiClient answerApiClient;

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
		CreateQuestionRequest request = new CreateQuestionRequest("example content", AdmissionType.SUSI,
			AdmissionCategory.ADMISSION_GUIDELINE, answerRequest);
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

	@DisplayName("Invalid 답변 필터링 테스트")
	@Test
	public void invalid_답변_저장_안됨() {
		// given
		AdmissionType admissionType = AdmissionType.SUSI;
		AdmissionCategory admissionCategory = AdmissionCategory.ADMISSION_GUIDELINE;
		String content = "명지대학교 크리스천리더 화공신소재환경공학부의 입시 결과";
		String contentToken = "명지대학교 크리스천리더 화공신소재환경공학부 입시 결과";

		LLMAnswerResponse invalidResponse = LLMAnswerResponse.of(
			AdmissionType.SUSI.getType(),
			AdmissionCategory.ADMISSION_GUIDELINE.getCategory(),
			Answer.of(
				question,
				"제공된 정보 내에서 명지대학교 크리스천리더 화공신소재환경공학부의 입시 결과에 대한 구체적인 정보를 찾을 수 없습니다. 더욱 자세한 상담을 원하시면 명지대학교 입학처 02-300-1799,1800으로 전화주시길 바랍니다."
			),
			List.of(AnswerReferenceResponse.of("테스트 title", "http://example.com/page=1"))
		);

		when(answerApiClient.askQuestion(any(LLMAskQuestionRequest.class))).thenReturn(Mono.just(invalidResponse));

		// when
		QuestionResponse response = answerManager.processNewQuestion(admissionType, admissionCategory, content,
			contentToken);

		// then
		assertThat(response).isNotNull();
		verify(answerRepository, times(0)).save(any(Answer.class));
	}

}
