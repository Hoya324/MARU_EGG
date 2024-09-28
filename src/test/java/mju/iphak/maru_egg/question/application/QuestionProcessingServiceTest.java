package mju.iphak.maru_egg.question.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.answer.application.AnswerApiClient;
import mju.iphak.maru_egg.answer.application.AnswerManager;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.domain.AnswerReference;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.common.utils.PhraseExtractionUtils;
import mju.iphak.maru_egg.question.dao.request.SelectQuestionCores;
import mju.iphak.maru_egg.question.dao.response.QuestionCore;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class QuestionProcessingServiceTest extends MockTest {

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private AnswerRepository answerRepository;

	@Mock
	private AnswerManager answerManager;

	@Mock
	private AnswerApiClient answerApiClient;

	@InjectMocks
	private QuestionProcessingService questionProcessingService;

	private MockWebServer mockWebServer;
	private Question question;
	private Answer answer;
	private List<AnswerReference> references;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		setupMockEntities();
		setupMockServer();
		setupAnswerManagerMock();
	}

	@AfterEach
	void shutdown() throws IOException {
		if (mockWebServer != null) {
			this.mockWebServer.shutdown();
		}
	}

	@DisplayName("질문을 조회하는데 성공한 경우")
	@Test
	void 질문_조회_성공() {
		// given
		String content = "테스트 질문입니다.";
		String contentToken = PhraseExtractionUtils.extractPhrases(content);
		SelectQuestionCores selectQuestionCores = SelectQuestionCores.of(QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE, content, contentToken);

		when(questionRepository.searchQuestions(eq(selectQuestionCores)))
			.thenReturn(Optional.of(List.of(QuestionCore.of(1L, contentToken))));

		// when
		QuestionResponse result = questionProcessingService.question(selectQuestionCores.type(),
			selectQuestionCores.category(), content);

		// then
		assertThat(result).isEqualTo(createExpectedQuestionResponse());
	}

	@DisplayName("질문이 없을 때 새로운 질문을 요청하는지 확인")
	@Test
	void 질문_없을_때_새로운_질문_요청() {
		// given
		String content = "새로운 질문입니다.";
		SelectQuestionCores selectQuestionCores = SelectQuestionCores.of(QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE, content, content);
		String contentToken = PhraseExtractionUtils.extractPhrases(content);

		when(questionRepository.searchQuestions(eq(selectQuestionCores)))
			.thenReturn(Optional.of(Collections.emptyList()));

		// when
		questionProcessingService.question(selectQuestionCores.type(), selectQuestionCores.category(), content);

		// then
		verify(answerManager, times(1)).processNewQuestion(eq(selectQuestionCores.type()),
			eq(selectQuestionCores.category()), eq(content), eq(contentToken));
	}

	@DisplayName("질문 조회 시 존재하지 않는 질문 ID로 인한 예외 처리")
	@Test
	void 질문_ID로_조회_시_존재하지_않는_질문_ID() {
		// given
		Long invalidQuestionId = 999L;
		when(questionRepository.findById(invalidQuestionId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(EntityNotFoundException.class, () -> {
			questionProcessingService.getQuestion(invalidQuestionId);
		});
	}

	@DisplayName("질문 검색 시 서버 내부 오류 발생하여 빈 배열 반환")
	@Test
	void 질문_검색_서버_내부_오류_빈배열_반환() {
		// given
		String contentToken = "서버 오류 테스트";
		SelectQuestionCores selectQuestionCores = SelectQuestionCores.of(QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE, contentToken, contentToken);
		List<QuestionCore> questionCores = List.of();

		when(questionRepository.searchQuestions(eq(selectQuestionCores)))
			.thenReturn(Optional.of(questionCores));

		// when
		questionProcessingService.question(selectQuestionCores.type(), selectQuestionCores.category(), "서버 오류 테스트");

		// then
		verify(answerManager, times(1)).processNewQuestion(eq(selectQuestionCores.type()),
			eq(selectQuestionCores.category()), eq("서버 오류 테스트"), eq(contentToken));
	}

	@DisplayName("MOCK LLM 서버에 질문을 요청합니다.")
	@Test
	void MOCK_LLM_질문_요청() {
		// given
		LLMAskQuestionRequest request = LLMAskQuestionRequest.of(QuestionType.SUSI.getType(),
			QuestionCategory.ADMISSION_GUIDELINE.getCategory(), question.getContent());

		// when
		LLMAnswerResponse result = mockAskQuestion(request);

		// then
		assertThat(result).isEqualTo(createExpectedLLMAnswerResponse());
	}

	private QuestionResponse createExpectedQuestionResponse() {
		return QuestionResponse.of(question, AnswerResponse.from(answer),
			List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link")));
	}

	private LLMAnswerResponse createExpectedLLMAnswerResponse() {
		return LLMAnswerResponse.of(QuestionType.SUSI.getType(), QuestionCategory.ADMISSION_GUIDELINE.getCategory(),
			Answer.of(question, "새로운 답변입니다."),
			List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link")));
	}

	private void setupMockEntities() {
		question = mock(Question.class);
		answer = mock(Answer.class);
		references = List.of(AnswerReference.of("테스트 title", "테스트 link", answer));

		when(question.getId()).thenReturn(1L);
		when(answer.getId()).thenReturn(1L);
		when(answer.getReferences()).thenReturn(references);

		when(answerManager.getAnswerByQuestionId(1L)).thenReturn(answer);
		when(answerRepository.findByQuestionId(anyLong())).thenReturn(Optional.of(answer));

		when(questionRepository.searchQuestions(any(SelectQuestionCores.class)))
			.thenReturn(Optional.of(List.of(QuestionCore.of(1L, "테스트 질문입니다."))));
		when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
	}

	private void setupMockServer() {
		this.mockWebServer = new MockWebServer();
		answerApiClient = new AnswerApiClient(WebClient.builder()
			.baseUrl(this.mockWebServer.url("/").toString())
			.clientConnector(new ReactorClientHttpConnector())
			.build());
	}

	private void setupAnswerManagerMock() {
		when(answerManager.getAnswerByQuestionId(anyLong())).thenReturn(answer);
	}

	private LLMAnswerResponse mockAskQuestion(LLMAskQuestionRequest request) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("questionType", request.questionType());
		formData.add("questionCategory", request.questionCategory());
		formData.add("question", request.question());

		List<AnswerReferenceResponse> references = List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link"));
		LLMAnswerResponse expectedResponse = LLMAnswerResponse.of(request.questionType(), request.questionCategory(),
			Answer.of(question, "새로운 답변입니다."), references);

		mockWebServer.enqueue(new MockResponse()
			.setHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
			.setHeader("Accept", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.setResponseCode(200)
			.setBody(new Gson().toJson(expectedResponse)));

		return answerApiClient.askQuestion(request).block();
	}
}