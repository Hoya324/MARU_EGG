package mju.iphak.maru_egg.answer.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.CreateAnswerRequest;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import reactor.core.publisher.Mono;

public class AnswerServiceTest extends MockTest {

	@Mock
	private AnswerRepository answerRepository;

	@Mock
	private ExchangeFunction exchangeFunction;

	@InjectMocks
	private AnswerService answerService;

	private Answer answer;
	private Question question;

	@Value("${LLM_BASE_URL}")
	private String llmBaseUrl;

	@Before
	public void setUp() {
		question = Question.of("수시 일정 알려주세요.", "수시 일정", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE);
		answer = Answer.of(question,
			"수시 일정은 2024년 12월 19일(목)부터 2024년 12월 26일(목) 18:00까지 최초합격자 발표가 있고, 2025년 2월 10일(월) 10:00부터 2025년 2월 12일(수) 15:00까지 문서등록 및 등록금 납부가 진행됩니다. 등록금 납부 기간은 2024년 12월 16일(월) 10:00부터 2024년 12월 18일(수) 15:00까지이며, 방법은 입학처 홈페이지를 통한 문서등록 및 등록금 납부를 하시면 됩니다. 상세 안내는 추후 입학처 홈페이지를 통해 공지될 예정입니다.");

		WebClient webClient = WebClient.builder()
			.exchangeFunction(exchangeFunction)
			.baseUrl(llmBaseUrl)
			.build();

		answerService = new AnswerService(answerRepository, webClient);

		ClientResponse clientResponse = ClientResponse.create(HttpStatusCode.valueOf(200))
			.header("Content-Type", "application/json")
			.body(String.format("{\"questionType\":\"%s\",\"questionCategory\":\"%s\",\"answer\":\"%s\"}",
				question.getQuestionType().getType(), question.getQuestionCategory().getCategory(),
				answer.getContent()))
			.build();

		when(exchangeFunction.exchange(any())).thenReturn(Mono.just(clientResponse));
	}

	@DisplayName("questionId로 답변을 조회합니다.")
	@Test
	public void 답변_조회_성공() {
		// given
		when(answerRepository.findByQuestionId(1L)).thenReturn(Optional.of(answer));

		// when
		Answer result = answerService.getAnswerByQuestionId(1L);

		// then
		assertNotNull(result);
		assertEquals(answer, result);
	}

	@DisplayName("questionId로 답변 조회에 실패한 경우.")
	@Test
	public void 답변_조회_실패_NOTFOUND() {
		// given
		when(answerRepository.findByQuestionId(1L)).thenReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			answerService.getAnswerByQuestionId(1L);
		});

		assertEquals("질문 id가 1인 답변을 찾을 수 없습니다.", exception.getMessage());
	}

	@DisplayName("LLM 서버에 질문을 요청합니다.")
	@Test
	public void LLM_질문_요청() {
		// given
		LLMAskQuestionRequest request = LLMAskQuestionRequest.of(QuestionType.SUSI.getType(),
			QuestionCategory.ADMISSION_GUIDELINE.getCategory(),
			question.getContent());
		List<AnswerReferenceResponse> references = new ArrayList<>(
			List.of(AnswerReferenceResponse.of("테스트 title", "테스트 link")));

		// when
		LLMAnswerResponse expectedResponse = LLMAnswerResponse.of(QuestionType.SUSI.getType(),
			QuestionCategory.ADMISSION_GUIDELINE.getCategory(), answer, references);
		LLMAnswerResponse result = answerService.askQuestion(request).block();

		// then
		assertThat(result).isNotNull();
		assertThat(result.answer()).isEqualTo(answer.getContent());
		assertThat(expectedResponse).isEqualTo(result);
	}

	@DisplayName("답변 내용을 수정에 성공한 경우")
	@Test
	public void 답변_내용_수정_성공() throws Exception {
		// given
		when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
		Long id = 1L;

		// when
		String updateContent = "변경된 답변";
		answerService.updateAnswerContent(id, updateContent);

		// then
		assertThat(answer.getContent()).isEqualTo(updateContent);
	}

	@DisplayName("질문 생성에 성공한 경우")
	@Test
	public void 답변_생성_성공() {
		// given
		CreateAnswerRequest answerRequest = new CreateAnswerRequest("example answer content", 2024);
		CreateQuestionRequest request = new CreateQuestionRequest("example content", QuestionType.SUSI,
			QuestionCategory.ADMISSION_GUIDELINE, answerRequest);
		Question question = request.toEntity();
		Answer answer = answerRequest.toEntity(question);

		when(answerRepository.save(answer))
			.thenReturn(answer);

		// when
		answerService.createAnswer(question, answerRequest);

		// then
		verify(answerRepository, times(1)).save(any(Answer.class));
	}
}