package mju.iphak.maru_egg.answer.application.create;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

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
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

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
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("[성공] 질문, 답변, 참고자료를 생성하고 QuestionResponse 반환")
	@Test
	void testCreateRAGAnswerSuccess() {
		// Given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;
		String content = "수시 일정 알려주세요.";
		String contentToken = "수시 일정";
		String answerContent = "수시 일정은 2024년 12월 19일부터 시작됩니다.";

		SaveRAGAnswerRequest request = SaveRAGAnswerRequest.builder()
			.type(type)
			.category(category)
			.content(content)
			.contentToken(contentToken)
			.answerContent(answerContent)
			.references(Collections.emptyList()) // 참고자료 없음
			.build();

		Question mockQuestion = Question.of(content, contentToken, type, category);
		Answer mockAnswer = Answer.of(mockQuestion, answerContent);

		when(createQuestionByTypeAndCategory.invoke(type, category, content, contentToken)).thenReturn(mockQuestion);
		when(createAnswer.invoke(mockQuestion, answerContent)).thenReturn(mockAnswer);

		// When
		QuestionResponse result = createRAGAnswerService.invoke(request);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(mockQuestion.getId());
		assertThat(result.content()).isEqualTo(content);
		assertThat(result.answer().content()).isEqualTo(answerContent);
		assertThat(result.references()).isEmpty();
	}

	@DisplayName("[성공] 질문, 답변, 참고자료 생성 및 참고자료 포함된 QuestionResponse 반환")
	@Test
	void testCreateRAGAnswerWithReferences() {
		// Given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;
		String content = "수시 일정 알려주세요.";
		String contentToken = "수시 일정";
		String answerContent = "수시 일정은 2024년 12월 19일부터 시작됩니다.";
		var references = Collections.singletonList(
			AnswerReferenceResponse.of("수시 일정 안내", "http://example.com")
		);

		SaveRAGAnswerRequest request = SaveRAGAnswerRequest.builder()
			.type(type)
			.category(category)
			.content(content)
			.contentToken(contentToken)
			.answerContent(answerContent)
			.references(references)
			.build();

		Question mockQuestion = Question.of(content, contentToken, type, category);
		Answer mockAnswer = Answer.of(mockQuestion, answerContent);

		when(createQuestionByTypeAndCategory.invoke(type, category, content, contentToken)).thenReturn(mockQuestion);
		when(createAnswer.invoke(mockQuestion, answerContent)).thenReturn(mockAnswer);

		// When
		QuestionResponse result = createRAGAnswerService.invoke(request);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(mockQuestion.getId());
		assertThat(result.content()).isEqualTo(content);
		assertThat(result.answer().content()).isEqualTo(answerContent);
		assertThat(result.references()).hasSize(1);
		assertThat(result.references().get(0).title()).isEqualTo("수시 일정 안내");
	}
}
