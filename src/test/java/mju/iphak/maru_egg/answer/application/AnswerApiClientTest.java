package mju.iphak.maru_egg.answer.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;

import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.question.domain.Question;
import reactor.core.publisher.Mono;

public class AnswerApiClientTest extends MockTest {

	@Mock
	private ExchangeFunction exchangeFunction;

	@InjectMocks
	private AnswerApiClient answerApiClient;

	@Value("${LLM_BASE_URL}")
	private String llmBaseUrl;

	private WebClient webClient;
	private Answer answer;
	private Question question;
	private ClientResponse clientResponse;

	@Before
	public void setUp() {
		question = createTestQuestion();
		answer = createTestAnswer(question);
		webClient = createWebClient();
		clientResponse = createClientResponse();

		answerApiClient = new AnswerApiClient(webClient);
		when(exchangeFunction.exchange(any())).thenReturn(Mono.just(clientResponse));
	}

	@DisplayName("LLM 서버에 질문을 요청합니다.")
	@Test
	public void LLM_질문_요청() {
		// given
		LLMAskQuestionRequest request = LLMAskQuestionRequest.of(
			AdmissionType.SUSI.getType(),
			AdmissionCategory.ADMISSION_GUIDELINE.getCategory(),
			question.getContent()
		);

		List<AnswerReferenceResponse> references = List.of(
			AnswerReferenceResponse.of("테스트 title", "테스트 link")
		);

		// when
		LLMAnswerResponse result = answerApiClient.askQuestion(request).block();

		// then
		assertThat(result).isNotNull();
		assertThat(result.questionType()).isEqualTo(question.getAdmissionType().getType());
		assertThat(result.questionCategory()).isEqualTo(question.getAdmissionCategory().getCategory());
		assertThat(result.references()).isNotNull();
	}

	private Question createTestQuestion() {
		return Question.of(
			"수시 일정 알려주세요.",
			"수시 일정",
			AdmissionType.SUSI,
			AdmissionCategory.ADMISSION_GUIDELINE
		);
	}

	private Answer createTestAnswer(Question question) {
		return Answer.of(
			question,
			"수시 일정은 2024년 12월 19일(목)부터 2024년 12월 26일(목) 18:00까지 최초합격자 발표가 있고, "
				+ "2025년 2월 10일(월) 10:00부터 2025년 2월 12일(수) 15:00까지 문서등록 및 등록금 납부가 진행됩니다. "
				+ "등록금 납부 기간은 2024년 12월 16일(월) 10:00부터 2024년 12월 18일(수) 15:00까지이며, "
				+ "방법은 입학처 홈페이지를 통한 문서등록 및 등록금 납부를 하시면 됩니다. 상세 안내는 추후 입학처 홈페이지를 통해 공지될 예정입니다."
		);
	}

	private WebClient createWebClient() {
		return WebClient.builder()
			.exchangeFunction(exchangeFunction)
			.baseUrl(llmBaseUrl)
			.build();
	}

	private ClientResponse createClientResponse() {
		return ClientResponse.create(HttpStatusCode.valueOf(200))
			.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
			.body(String.format(
				"{\"questionType\":\"%s\",\"questionCategory\":\"%s\",\"answer\":\"%s\"}",
				question.getAdmissionType().getType(),
				question.getAdmissionCategory().getCategory(),
				answer.getContent()
			))
			.build();
	}
}