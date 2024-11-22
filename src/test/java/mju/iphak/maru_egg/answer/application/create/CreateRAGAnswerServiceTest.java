package mju.iphak.maru_egg.answer.application.create;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answerreference.application.create.BatchCreateAnswerReference;
import mju.iphak.maru_egg.answerreference.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.application.create.CreateQuestionByTypeAndCategory;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.request.SaveRAGAnswerRequest;

class CreateRAGAnswerServiceTest extends MockTest {

	@Mock
	private CreateQuestionByTypeAndCategory createQuestionByTypeAndCategory;

	@Mock
	private CreateAnswer createAnswer;

	@Mock
	private BatchCreateAnswerReference createAnswerReference;

	@InjectMocks
	private CreateRAGAnswerService createRAGAnswerService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	@DisplayName("[성공] 질문과 답변 생성")
	@Test
	void 질문_답변_생성() {
		// given
		List<AnswerReferenceResponse> references = Collections.emptyList();
		SaveRAGAnswerRequest request = createRequest(references);
		Question mockQuestion = createMockQuestion(request);
		Answer mockAnswer = createMockAnswer(mockQuestion, request.answerContent());

		when(createQuestionByTypeAndCategory.invoke(
			request.type(), request.category(), request.content(), request.contentToken())
		).thenReturn(mockQuestion);
		when(createAnswer.invoke(mockQuestion, request.answerContent())).thenReturn(mockAnswer);

		// when
		createRAGAnswerService.invoke(request);

		// then
		verify(createQuestionByTypeAndCategory, times(1)).invoke(
			request.type(), request.category(), request.content(), request.contentToken()
		);
		verify(createAnswer, times(1)).invoke(mockQuestion, request.answerContent());
		verify(createAnswerReference, times(1)).invoke(mockAnswer, request.references());
	}

	private SaveRAGAnswerRequest createRequest(List<AnswerReferenceResponse> references) {
		return SaveRAGAnswerRequest.builder()
			.type(AdmissionType.SUSI)
			.category(AdmissionCategory.ADMISSION_GUIDELINE)
			.content("수시 일정 알려주세요.")
			.contentToken("수시 일정")
			.answerContent("수시 일정은 2024년 12월 19일부터 시작됩니다.")
			.references(references)
			.build();
	}

	private Question createMockQuestion(SaveRAGAnswerRequest request) {
		return Question.of(
			request.content(),
			request.contentToken(),
			request.type(),
			request.category()
		);
	}

	private Answer createMockAnswer(Question question, String answerContent) {
		return Answer.of(question, answerContent);
	}
}
