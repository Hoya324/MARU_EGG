package mju.iphak.maru_egg.question.application;

import static org.assertj.core.api.Assertions.*;
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

import mju.iphak.maru_egg.answer.application.AnswerApiClient;
import mju.iphak.maru_egg.answer.application.AnswerManager;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.domain.AnswerReference;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerReferenceRepository;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.common.utils.PhraseExtractionUtils;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
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
	private AnswerReferenceRepository answerReferenceRepository;

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
		questionProcessingService = new QuestionProcessingService(questionRepository, answerManager);
	}

	@AfterEach
	void shutdown() throws IOException {
		if (mockWebServer != null) {
			this.mockWebServer.shutdown();
		}
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

		when(questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(anyString(), any(QuestionType.class),
			any(QuestionCategory.class)))
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

	@DisplayName("질문을 조회하는데 성공한 경우")
	@Test
	void 질문_조회_성공() {
		// given
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
		String content = "테스트 질문입니다.";
		String contentToken = PhraseExtractionUtils.extractPhrases(content);

		when(questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(anyString(), eq(type), eq(category)))
			.thenReturn(Optional.of(List.of(QuestionCore.of(1L, contentToken))));

		// when
		QuestionResponse result = questionProcessingService.question(type, category, content);

		// then
		assertThat(result).isEqualTo(createExpectedQuestionResponse());
	}

	@DisplayName("질문이 없을 때 새로운 질문을 요청하는지 확인")
	@Test
	void 질문_없을_때_새로운_질문_요청() {
		// given
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
		String content = "새로운 질문입니다.";
		String contentToken = PhraseExtractionUtils.extractPhrases(content);

		when(questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(eq(contentToken), eq(type),
			eq(category)))
			.thenReturn(Optional.of(Collections.emptyList()));

		// when
		questionProcessingService.question(type, category, content);

		// then
		verify(answerManager, times(1)).processNewQuestion(eq(type), eq(category), eq(content), eq(contentToken));
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
}