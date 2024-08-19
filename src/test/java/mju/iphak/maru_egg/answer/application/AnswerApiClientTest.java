package mju.iphak.maru_egg.answer.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

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

import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.request.LLMAskQuestionRequest;
import mju.iphak.maru_egg.answer.dto.response.AnswerReferenceResponse;
import mju.iphak.maru_egg.answer.dto.response.LLMAnswerResponse;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import reactor.core.publisher.Mono;

public class AnswerApiClientTest extends MockTest {

	@Mock
	private ExchangeFunction exchangeFunction;

	@InjectMocks
	private AnswerApiClient answerApiClient;

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

		answerApiClient = new AnswerApiClient(webClient);

		ClientResponse clientResponse = ClientResponse.create(HttpStatusCode.valueOf(200))
			.header("Content-Type", "application/json")
			.body(String.format("{\"questionType\":\"%s\",\"questionCategory\":\"%s\",\"answer\":\"%s\"}",
				question.getQuestionType().getType(), question.getQuestionCategory().getCategory(),
				answer.getContent()))
			.build();

		when(exchangeFunction.exchange(any())).thenReturn(Mono.just(clientResponse));
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
		LLMAnswerResponse result = answerApiClient.askQuestion(request).block();

		// then
		assertThat(result).isNotNull();
		assertThat(result.answer()).isEqualTo(answer.getContent());
		assertThat(expectedResponse).isEqualTo(result);
	}
}