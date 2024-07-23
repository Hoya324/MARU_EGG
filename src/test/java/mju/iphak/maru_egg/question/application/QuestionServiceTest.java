package mju.iphak.maru_egg.question.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
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

import mju.iphak.maru_egg.answer.application.AnswerService;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
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
	private AnswerService answerService;
	private MockWebServer mockWebServer;

	@InjectMocks
	private QuestionService questionService;

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

		return answerService.askQuestion(request).block();
	}

	void startServer(ClientHttpConnector connector) {
		this.mockWebServer = new MockWebServer();
		answerService = new AnswerService(answerRepository, WebClient
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

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		question = mock(Question.class);
		answer = mock(Answer.class);

		when(question.getId()).thenReturn(1L);
		when(answer.getId()).thenReturn(1L);
		when(answerService.getAnswerByQuestionId(1L)).thenReturn(answer);
		when(answerRepository.findByQuestionId(anyLong())).thenReturn(Optional.of(answer));
		when(questionRepository.searchQuestionsByContentTokenAndTypeAndCategory(anyString(), any(QuestionType.class),
			any(QuestionCategory.class)))
			.thenReturn(Optional.of(List.of(QuestionCore.of(1L, "테스트 질문입니다."))));
		when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
		questionService = new QuestionService(questionRepository, answerService);

		doAnswer(invocation -> {
			LLMAskQuestionRequest request = invocation.getArgument(0);
			return Mono.just(mockAskQuestion(request));
		}).when(answerService).askQuestion(any(LLMAskQuestionRequest.class));
	}

	@DisplayName("질문 목록을 조회하는데 성공한 경우")
	@Test
	void 질문_목록_조회_성공() {
		// given
		QuestionType type = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;

		List<Question> questions = List.of(question);
		when(
			questionRepository.findAllByQuestionTypeAndQuestionCategoryOrderByViewCountDesc(type, category)).thenReturn(
			questions);
		when(answerService.getAnswerByQuestionId(question.getId())).thenReturn(answer);

		// when
		List<QuestionListItemResponse> result = questionService.getQuestions(type, category);

		// then
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertThat(result.get(0).content()).isEqualTo(question.getContent());
		assertThat(result.get(0).answer().content()).isEqualTo(answer.getContent());
	}

	@DisplayName("질문 목록을 조회하는데 성공한 경우 - category 없이 type으로 조회")
	@Test
	void 질문_목록_조회_성공_카테고리_없이() {
		// given
		QuestionType type = QuestionType.SUSI;

		List<Question> questions = List.of(question);
		when(questionRepository.findAllByQuestionTypeOrderByViewCountDesc(type)).thenReturn(questions);
		when(answerService.getAnswerByQuestionId(question.getId())).thenReturn(answer);

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
		Integer size = 5;
		Pageable pageable = PageRequest.of(0, size);

		List<Question> questions = List.of(question);
		SearchedQuestionsResponse searchedQuestionsResponse = new SearchedQuestionsResponse(1L, "example content");
		SliceQuestionResponse<SearchedQuestionsResponse> expectedResponse = new SliceQuestionResponse<>(
			List.of(searchedQuestionsResponse), 0, size, false, null, null);

		when(questionRepository.searchQuestionsOfCursorPagingByContent(content, cursorViewCount, questionId, pageable))
			.thenReturn(expectedResponse);

		// when
		SliceQuestionResponse<SearchedQuestionsResponse> result = questionService.searchQuestionsOfCursorPaging(content,
			cursorViewCount, questionId, size);

		// then
		assertNotNull(result);
		assertFalse(result.data().isEmpty());
		assertThat(result.data().get(0).content()).isEqualTo("example content");
		assertThat(result.hasNext()).isFalse();
	}
}