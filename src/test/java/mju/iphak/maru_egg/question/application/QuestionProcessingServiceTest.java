package mju.iphak.maru_egg.question.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;

import mju.iphak.maru_egg.answer.application.AnswerApiClient;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.domain.AnswerReference;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerReferenceRepository;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;
import mju.iphak.maru_egg.question.utils.PhraseExtractionUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;

class QuestionProcessingServiceTest extends MockTest {

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private AnswerRepository answerRepository;

	@Mock
	private AnswerReferenceRepository answerReferenceRepository;

	@Mock
	private AnswerApiClient answerApiClient;
	private MockWebServer mockWebServer;

	@InjectMocks
	private QuestionProcessingService questionProcessingService;

	private LLMAnswerResponse mockAskQuestion(LLMAskQuestionRequest request) {
		startServer(new ReactorClientHttpConnector());
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("questionType", request.questionType());
		formData.add("questionCategory", request.questionCategory());
		formData.add("question", request.question());
		Question testQuestion = Question.of("새로운 질문입니다.", "새로운 질문", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE);
		List<AnswerReferenceResponse> references = new ArrayList<>(
			List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link")));
		LLMAnswerResponse expectedResponse = LLMAnswerResponse.of(QuestionType.SUSI.getType(),
			QuestionCategory.ADMISSION_GUIDELINE.getCategory(), Answer.of(testQuestion, "새로운 답변입니다."), references);

		mockWebServer.enqueue(new MockResponse()
			.setHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
			.setHeader("Accept", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.setResponseCode(200)
			.setBody(new Gson().toJson(expectedResponse))
		);

		return answerApiClient.askQuestion(request).block();
	}

	void startServer(ClientHttpConnector connector) {
		this.mockWebServer = new MockWebServer();
		answerApiClient = new AnswerApiClient(answerRepository, WebClient
			.builder()
			.baseUrl(this.mockWebServer.url("/").toString())
			.clientConnector(connector).build()
		);
	}

	@AfterEach
	void shutdown() throws IOException {
		if (mockWebServer != null) {
			this.mockWebServer.shutdown();
		}
	}

	private Question question;
	private Answer answer;
	private AnswerReference answerReference;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		question = mock(Question.class);
		answer = mock(Answer.class);
		answerReference = mock(AnswerReference.class);

		List<AnswerReference> references = new ArrayList<>(
			List.of(AnswerReference.of("테스트 title", "테스트 link", answer)));
		when(question.getId()).thenReturn(1L);
		when(answer.getId()).thenReturn(1L);
		when(answer.getReferences()).thenReturn(references);
		when(answerApiClient.getAnswerByQuestionId(1L)).thenReturn(answer);
		when(answerRepository.findByQuestionId(anyLong())).thenReturn(Optional.of(answer));
		when(questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(anyString(), any(QuestionType.class),
			any(QuestionCategory.class)))
			.thenReturn(Optional.of(List.of(QuestionCore.of(1L, "테스트 질문입니다."))));
		when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

		questionProcessingService = new QuestionProcessingService(questionRepository, answerApiClient,
			answerReferenceRepository);

		doAnswer(invocation -> {
			LLMAskQuestionRequest request = invocation.getArgument(0);
			return Mono.just(mockAskQuestion(request));
		}).when(answerApiClient).askQuestion(any(LLMAskQuestionRequest.class));
	}

	@DisplayName("질문을 조회하는데 성공한 경우")
	@Test
	void 질문_조회_성공() {
		// given
		startServer(new ReactorClientHttpConnector());
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
		String content = "테스트 질문입니다.";
		String contentToken = PhraseExtractionUtils.extractPhrases(content);
		List<AnswerReferenceResponse> references = new ArrayList<>(
			List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link")));

		when(questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(anyString(), eq(type), eq(category)))
			.thenReturn(Optional.of(List.of(QuestionCore.of(1L, contentToken))));

		// when
		QuestionResponse result = questionProcessingService.question(type, category, content);

		// then
		assertThat(result).isEqualTo(QuestionResponse.of(question, AnswerResponse.from(answer), references));
	}

	// TODO: 추후 test
	// @DisplayName("유사한 질문을 찾지 못한 경우 새로운 질문을 생성")
	// @Test
	// void 유사한_질문_없음_새로운_질문_생성() {
	// 	// given
	// 	QuestionType type = QuestionType.SUSI;
	// 	QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
	// 	String content = "새로운 질문입니다.";
	// 	String contentToken = "새로운 질문";
	// 	Question testQuestion = Question.of(content, contentToken, type, category);
	// 	Answer testAnswer = Answer.of(testQuestion, "새로운 답변입니다.");
	//
	// 	when(questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(contentToken, type, category))
	// 		.thenReturn(Optional.of(Collections.emptyList()));
	// 	when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);
	// 	when(answerService.saveAnswer(any(Answer.class))).thenReturn(testAnswer);
	// 	doAnswer(invocation -> {
	// 		LLMAskQuestionRequest request = invocation.getArgument(0);
	// 		return Mono.just(mockAskQuestion(request));
	// 	}).when(answerService).askQuestion(any(LLMAskQuestionRequest.class));
	//
	// 	// when
	// 	QuestionResponse result = questionService.question(type, category, content);
	//
	// 	// then
	// 	verify(questionRepository, times(1)).searchQuestionsByContentTokenAndTypeAndCategory(anyString(), eq(type),
	// 		eq(category));
	// 	verify(answerService, times(1)).askQuestion(any(LLMAskQuestionRequest.class));
	// }

	// @DisplayName("유사한 질문이 있는 경우 기존 질문을 반환")
	// @Test
	// void 유사한_질문_있음_기존_질문_반환() throws Exception {
	// 	// given
	// 	QuestionType type = QuestionType.SUSI;
	// 	QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
	// 	String content = "유사한 테스트 질문입니다.";
	// 	String contentToken = PhraseExtractionUtils.extractPhrases(content);
	// 	double similarityScore = 0.98;
	// 	Map<CharSequence, Integer> tfIdfDummyMap = new HashMap<>();
	// 	tfIdfDummyMap.put("질문", 1);
	//
	// 	when(questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(anyString(), eq(type), eq(category)))
	// 		.thenReturn(Optional.of(List.of(QuestionCoreDTO.of(1L, contentToken))));
	// 	when(TextSimilarityUtils.computeTfIdf(anyList(), anyString())).thenReturn(tfIdfDummyMap);
	// 	when(TextSimilarityUtils.computeCosineSimilarity(anyMap(), anyMap())).thenReturn(similarityScore);
	// 	// when
	// 	QuestionResponse result = questionService.question(type, category, content);
	//
	// 	// then
	// 	assertNotNull(result);
	// 	assertEquals("테스트 답변입니다.", result.answer().content());
	// }

	@DisplayName("질문이 없을 때 새로운 질문을 요청하는지 확인")
	@Test
	void 질문_없을_때_새로운_질문_요청() {
		// given
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
		String content = "새로운 질문입니다.";
		String contentToken = PhraseExtractionUtils.extractPhrases(content);
		Question testQuestion = Question.of(content, contentToken, type, category);
		Answer testAnswer = Answer.of(testQuestion, "새로운 답변입니다.");
		List<AnswerReferenceResponse> references = new ArrayList<>(
			List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link")));
		LLMAnswerResponse expectedResponse = LLMAnswerResponse.of(QuestionType.SUSI.getType(),
			QuestionCategory.ADMISSION_GUIDELINE.getCategory(), testAnswer, references);

		when(questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(contentToken, type, category))
			.thenReturn(Optional.of(Collections.emptyList()));
		when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);
		when(answerApiClient.saveAnswer(any(Answer.class))).thenReturn(testAnswer);
		doAnswer(invocation -> {
			LLMAskQuestionRequest request = invocation.getArgument(0);
			return Mono.just(mockAskQuestion(request));
		}).when(answerApiClient).askQuestion(any(LLMAskQuestionRequest.class));

		// when
		QuestionResponse result = questionProcessingService.question(type, category, content);

		// then
		assertNotNull(result);
		assertThat(result.answer().content()).isEqualTo(expectedResponse.answer());
	}

	@DisplayName("MOCK LLM 서버에 질문을 요청합니다.")
	@Test
	public void MOCK_LLM_질문_요청() {
		// given
		startServer(new ReactorClientHttpConnector());
		LLMAskQuestionRequest request = LLMAskQuestionRequest.of(QuestionType.SUSI.getType(),
			QuestionCategory.ADMISSION_GUIDELINE.getCategory(),
			question.getContent());
		Question testQuestion = Question.of("새로운 질문입니다.", "새로운 질문", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE);
		List<AnswerReferenceResponse> references = new ArrayList<>(
			List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link")));
		LLMAnswerResponse expectedResponse = LLMAnswerResponse.of(QuestionType.SUSI.getType(),
			QuestionCategory.ADMISSION_GUIDELINE.getCategory(), Answer.of(testQuestion, "새로운 답변입니다."), references);

		// when
		LLMAnswerResponse result = mockAskQuestion(request);

		// then
		assertThat(result).isEqualTo(expectedResponse);
	}

}
