package mju.iphak.maru_egg.answer.application.process;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import mju.iphak.maru_egg.answer.application.create.CreateRAGAnswer;
import mju.iphak.maru_egg.answer.application.rag.RagAnswer;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.request.SaveRAGAnswerRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import reactor.core.publisher.Mono;

class ProcessAnswerServiceTest extends MockTest {

	@Mock
	private RagAnswer ragAnswer;

	@Mock
	private CreateRAGAnswer createRAGAnswer;

	@InjectMocks
	private ProcessAnswerService processAnswerService;

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("[성공] RAG 서버에서 유효한 답변을 받아 질문과 답변 저장")
	@Test
	void testValidRagResponse() {
		// Given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;
		String content = "수시 일정 알려주세요.";
		String contentToken = "수시 일정";

		Question question = Question.of(content, contentToken, type, category);
		Answer answer = Answer.of(
			question,
			"수시 일정은 2024년 12월 19일부터 12월 26일까지 최초합격자 발표가 있습니다. "
				+ "등록금 납부 기간은 12월 16일부터 12월 18일까지입니다."
		);

		LLMAnswerResponse mockRagResponse = LLMAnswerResponse.of(
			type.getType(),
			category.getCategory(),
			answer,
			null
		);

		SaveRAGAnswerRequest expectedSaveRequest = SaveRAGAnswerRequest.builder()
			.type(type)
			.category(category)
			.content(content)
			.contentToken(contentToken)
			.answerContent(answer.getContent())
			.references(Collections.emptyList())
			.build();

		QuestionResponse mockQuestionResponse = QuestionResponse.builder()
			.id(1L)
			.content(content)
			.answer(AnswerResponse.builder()
				.content(answer.getContent())
				.build())
			.references(Collections.emptyList())
			.build();

		when(ragAnswer.invoke(any(LLMAskQuestionRequest.class))).thenReturn(Mono.just(mockRagResponse));
		when(createRAGAnswer.invoke(expectedSaveRequest)).thenReturn(mockQuestionResponse);

		// When
		QuestionResponse result = processAnswerService.invoke(type, category, content, contentToken);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(1L);
		assertThat(result.content()).isEqualTo(content);
		assertThat(result.answer().content()).isEqualTo(answer.getContent());
	}
}
