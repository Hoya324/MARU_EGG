package mju.iphak.maru_egg.question.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.answer.application.AnswerApiClient;
import mju.iphak.maru_egg.answer.application.AnswerManager;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.dao.request.SelectQuestionCores;
import mju.iphak.maru_egg.question.dao.request.SelectQuestions;
import mju.iphak.maru_egg.question.dao.response.QuestionCore;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionListItemResponse;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.core.publisher.Mono;

class QuestionServiceTest extends MockTest {

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private AnswerRepository answerRepository;

	@Mock
	private AnswerManager answerManager;

	@Mock
	private AnswerApiClient answerApiClient;

	@InjectMocks
	private QuestionService questionService;

	private MockWebServer mockWebServer;
	private Question question;
	private Answer answer;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		question = mock(Question.class);
		answer = mock(Answer.class);

		when(question.getId()).thenReturn(1L);
		when(answer.getId()).thenReturn(1L);
		when(answerManager.getAnswerByQuestionId(1L)).thenReturn(answer);
		when(answerRepository.findByQuestionId(anyLong())).thenReturn(Optional.of(answer));
		when(questionRepository.searchQuestions(any(SelectQuestionCores.class)))
			.thenReturn(Optional.of(List.of(QuestionCore.of(1L, "테스트 질문입니다."))));
		when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
		questionService = new QuestionService(questionRepository, answerManager);
		configureAnswerApiClient();
	}

	@AfterEach
	void tearDown() throws IOException {
		if (mockWebServer != null) {
			mockWebServer.shutdown();
		}
	}

	@DisplayName("질문 목록을 조회하는데 성공한 경우")
	@Test
	void 질문_목록_조회_성공() {
		// given
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;

		when(questionRepository.findAllByQuestionTypeAndQuestionCategoryOrderByViewCountDesc(type, category))
			.thenReturn(List.of(question));
		when(answerManager.getAnswerByQuestionId(question.getId())).thenReturn(answer);

		// when
		List<QuestionListItemResponse> result = questionService.getQuestions(type, category);

		// then
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertThat(result.get(0).content()).isEqualTo(question.getContent());
		assertThat(result.get(0).answer().content()).isEqualTo(answer.getContent());
	}

	@DisplayName("질문 목록을 조회하는데 성공한 경우 - 카테고리 없이 타입으로 조회")
	@Test
	void 질문_목록_조회_성공_카테고리_없이() {
		// given
		QuestionType type = QuestionType.SUSI;

		when(questionRepository.findAllByQuestionTypeOrderByViewCountDesc(type))
			.thenReturn(List.of(question));
		when(answerManager.getAnswerByQuestionId(question.getId())).thenReturn(answer);

		// when
		List<QuestionListItemResponse> result = questionService.getQuestions(type, null);

		// then
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertThat(result.get(0).content()).isEqualTo(question.getContent());
		assertThat(result.get(0).answer().content()).isEqualTo(answer.getContent());
	}

	@DisplayName("질문 자동완성 조회에 성공한 경우")
	@Test
	void 질문_자동완성_조회_성공() {
		// given
		String content = "example content";
		Integer cursorViewCount = 0;
		Long questionId = 0L;
		int size = 5;
		Pageable pageable = PageRequest.of(0, size);
		SelectQuestions selectQuestions = SelectQuestions.of(QuestionType.SUSI, QuestionCategory.ADMISSION_GUIDELINE,
			content, cursorViewCount, questionId, pageable);

		SearchedQuestionsResponse searchedQuestionsResponse = new SearchedQuestionsResponse(1L, "example content",
			true);
		SliceQuestionResponse<SearchedQuestionsResponse> expectedResponse = new SliceQuestionResponse<>(
			List.of(searchedQuestionsResponse), 0, size, false, null, null);

		when(questionRepository.searchQuestionsOfCursorPaging(selectQuestions)).thenReturn(expectedResponse);

		// when
		SliceQuestionResponse<SearchedQuestionsResponse> result = questionService.searchQuestionsOfCursorPaging(
			selectQuestions.type(),
			selectQuestions.category(), content,
			cursorViewCount, questionId, size);

		// then
		assertNotNull(result);
		assertFalse(result.data().isEmpty());
		assertFalse(result.hasNext());
		assertThat(result.data().get(0).content()).isEqualTo("example content");
	}

	@DisplayName("질문 생성에 성공한 경우")
	@Test
	void 질문_생성_성공() {
		// given
		CreateAnswerRequest answerRequest = new CreateAnswerRequest("example answer content", 2024);
		CreateQuestionRequest request = new CreateQuestionRequest("example content", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE, answerRequest);
		Question question = request.toEntity();

		when(questionRepository.save(question)).thenReturn(question);

		// when
		questionService.createQuestion(request);

		// then
		verify(questionRepository, times(1)).save(any(Question.class));
		verify(answerManager, times(1)).createAnswer(any(Question.class), eq(answerRequest));
	}

	@DisplayName("질문 삭제에 성공한 경우")
	@Test
	void 질문_삭제_성공() {
		// given
		Long id = 1L;
		Question question = Question.of("질문", "질문", QuestionType.SUSI, QuestionCategory.ADMISSION_GUIDELINE);
		when(questionRepository.findById(id)).thenReturn(Optional.ofNullable(question));

		// when
		questionService.deleteQuestion(id);

		// then
		verify(questionRepository, times(1)).findById(id);
		verify(questionRepository, times(1)).delete(any(Question.class));
	}

	@DisplayName("질문 삭제시에 조회 실패")
	@Test
	public void 질문_삭제시_조회_실패_NOTFOUND() {
		// given
		Long id = 1L;
		when(questionRepository.findById(id)).thenReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			questionService.deleteQuestion(id);
		});

		assertThat("id: 1인 질문을 찾을 수 없습니다.").isEqualTo(exception.getMessage());
	}

	@DisplayName("답변 내용 수정 실패")
	@Test
	public void 답변_내용_수정_실패_NOTFOUND() {
		// given
		Long id = 1L;
		when(questionRepository.findById(id)).thenReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			questionService.updateQuestionContent(id, "새로운 내용");
		});

		assertThat("id: 1인 질문을 찾을 수 없습니다.").isEqualTo(exception.getMessage());
	}

	private void configureAnswerApiClient() {
		doAnswer(invocation -> {
			LLMAskQuestionRequest request = invocation.getArgument(0);
			return Mono.just(mockAskQuestion(request));
		}).when(answerApiClient).askQuestion(any(LLMAskQuestionRequest.class));
	}

	private LLMAnswerResponse mockAskQuestion(LLMAskQuestionRequest request) {
		startServer(new ReactorClientHttpConnector());
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("questionType", request.questionType());
		formData.add("questionCategory", request.questionCategory());
		formData.add("question", request.question());

		Question testQuestion = Question.of("새로운 질문입니다.", "새로운 질문", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE);
		List<AnswerReferenceResponse> references = List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link"));
		LLMAnswerResponse expectedResponse = LLMAnswerResponse.of(QuestionType.SUSI.getType(),
			QuestionCategory.ADMISSION_GUIDELINE.getCategory(), Answer.of(testQuestion, "새로운 답변입니다."), references);

		mockWebServer.enqueue(new MockResponse()
			.setHeader("Content-type", MediaType.APPLICATION_JSON_VALUE)
			.setResponseCode(200)
			.setBody(new Gson().toJson(expectedResponse)));

		return answerApiClient.askQuestion(request).block();
	}

	private void startServer(ClientHttpConnector connector) {
		this.mockWebServer = new MockWebServer();
		answerApiClient = new AnswerApiClient(WebClient.builder()
			.baseUrl(this.mockWebServer.url("/").toString())
			.clientConnector(connector).build());
	}

}