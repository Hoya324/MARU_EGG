package mju.iphak.maru_egg.answer.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerReferenceRepository;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;
import reactor.core.publisher.Mono;

class AnswerManagerTest {

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private AnswerApiClient answerApiClient;

	@Mock
	private AnswerReferenceRepository answerReferenceRepository;

	@InjectMocks
	private AnswerManager answerManager;

	private Answer answer;

	@BeforeEach
	void setUp() {
		answer = mock(Answer.class);
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("LLM에서 유효한 답변을 받을 경우")
	@Test
	void LLM에서_유효한_답변() {
		// Given
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
		String content = "입학 안내는 무엇인가요?";
		String contentToken = "입학 안내";

		Question newQuestion = Question.of(content, contentToken, type, category);
		Answer newAnswer = Answer.of(newQuestion, "입학 안내는 다음과 같습니다.");
		List<AnswerReferenceResponse> references = List.of(
			AnswerReferenceResponse.of("참조 자료 제목", "http://example.com")
		);

		LLMAnswerResponse llmAnswerResponse = LLMAnswerResponse.of(
			type.getType(), category.getCategory(), newAnswer, references
		);

		when(questionRepository.save(any(Question.class))).thenReturn(newQuestion);
		when(answerApiClient.askQuestion(any(LLMAskQuestionRequest.class))).thenReturn(Mono.just(llmAnswerResponse));
		when(answerApiClient.saveAnswer(any(Answer.class))).thenReturn(newAnswer);

		// When
		QuestionResponse result = answerManager.processNewQuestion(type, category, content, contentToken);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.content()).isEqualTo(content);
		assertThat(result.answer().content()).isEqualTo(newAnswer.getContent());
		assertThat(result.references()).hasSize(1);
		assertThat(result.references().get(0).title()).isEqualTo("참조 자료 제목");
		assertThat(result.references().get(0).link()).isEqualTo("http://example.com");

		verify(questionRepository, times(1)).save(any(Question.class));
		verify(answerApiClient, times(1)).askQuestion(any(LLMAskQuestionRequest.class));
		verify(answerApiClient, times(1)).saveAnswer(any(Answer.class));
		verify(answerReferenceRepository, times(1)).saveAll(anyList());
	}

	@DisplayName("LLM에서 유효하지 않은 답변을 받을 경우")
	@Test
	void LLM_유효하지_않은_답변() {
		// Given
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
		String content = "입학 안내는 무엇인가요?";
		String contentToken = "입학 안내";

		LLMAnswerResponse llmAnswerResponse = LLMAnswerResponse.of(
			type.getType(), category.getCategory(), answer, List.of()
		);

		when(answerApiClient.askQuestion(any(LLMAskQuestionRequest.class))).thenReturn(Mono.just(llmAnswerResponse));

		// When
		QuestionResponse result = answerManager.processNewQuestion(type, category, content, contentToken);

		// Then
		assertThat(result).isNotNull();

		verify(questionRepository, never()).save(any(Question.class));
		verify(answerApiClient, times(1)).askQuestion(any(LLMAskQuestionRequest.class));
		verify(answerApiClient, never()).saveAnswer(any(Answer.class));
		verify(answerReferenceRepository, never()).saveAll(anyList());
	}
}