package mju.iphak.maru_egg.question.application.process;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.application.process.ProcessAnswer;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.common.utils.PhraseExtractionUtils;
import mju.iphak.maru_egg.question.application.find.FindMostSimilarQuestionId;
import mju.iphak.maru_egg.question.application.find.FindQuestion;
import mju.iphak.maru_egg.question.dao.request.QuestionCoreDAO;
import mju.iphak.maru_egg.question.dao.response.QuestionCore;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

class ProcessQuestionServiceTest extends MockTest {

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private ProcessAnswer processAnswer;

	@Mock
	private FindQuestion findQuestion;

	@Mock
	private FindMostSimilarQuestionId findMostSimilarQuestionId;

	@InjectMocks
	private ProcessQuestionService processQuestionService;

	private Question question;
	private Answer answer;
	private AnswerResponse answerResponse;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		question = Question.of("테스트 질문입니다.", "테스트 질문", AdmissionType.JEONGSI,
			AdmissionCategory.ADMISSION_GUIDELINE);
		answer = Answer.of(question, "기존 답변 내용");
		answerResponse = AnswerResponse.from(answer);
	}

	@DisplayName("[성공 case1(질문 없음)] LLM 서버에 질문 요청")
	@Test
	void 질문_없을_때_LLM서버에_질문요청() {
		// given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;
		String content = "명지대학교 입시 결과";
		String contentToken = PhraseExtractionUtils.extractPhrases(content); // 실제 호출

		QuestionCoreDAO questionCoreDAO = QuestionCoreDAO.of(type, category, content, contentToken);
		when(questionRepository.searchQuestions(questionCoreDAO)).thenReturn(Optional.empty());
		when(processAnswer.invoke(type, category, content, contentToken))
			.thenReturn(QuestionResponse.valueOfInvalidQuestion(content, "답변이 없습니다."));

		// when
		QuestionResponse response = processQuestionService.invoke(type, category, content);

		// then
		verify(processAnswer, times(1)).invoke(type, category, content, contentToken);
		verify(questionRepository, times(1)).searchQuestions(questionCoreDAO);
		assertThat(response).isNotNull();
		assertThat(response.content()).isEqualTo(content);
		assertThat(response.answer().content()).isEqualTo("답변이 없습니다.");
	}

	@DisplayName("[성공 case2(질문 재사용)] 유사 질문 DB에서 재사용")
	@Test
	void 유사질문_DB_재사용() {
		// given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;
		String content = "명지대학교 입시 결과";
		String contentToken = PhraseExtractionUtils.extractPhrases(content);

		QuestionCoreDAO questionCoreDAO = QuestionCoreDAO.of(type, category, content, contentToken);
		QuestionCore similarQuestionCore = QuestionCore.of(1L, "명지대학교 입시 결과");
		List<QuestionCore> questionCores = List.of(similarQuestionCore);

		// Stubbing
		when(questionRepository.searchQuestions(questionCoreDAO)).thenReturn(Optional.of(questionCores));
		when(findMostSimilarQuestionId.invoke(questionCores, contentToken)).thenReturn(1L);
		when(findQuestion.invoke(1L))
			.thenReturn(QuestionResponse.of(question, answerResponse, Collections.emptyList()));

		// when
		QuestionResponse response = processQuestionService.invoke(type, category, content);

		// then
		verify(findMostSimilarQuestionId, times(1)).invoke(questionCores, contentToken);
		verify(findQuestion, times(1)).invoke(1L);
		assertThat(response).isNotNull();
		assertThat(response.content()).isEqualTo(question.getContent());
	}

	@DisplayName("[성공 case3(질문 새로 요청)] 유사 질문 없음")
	@Test
	void 유사질문_없을_때_새로_요청() {
		// given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;
		String content = "입학 관련 정보";
		String contentToken = PhraseExtractionUtils.extractPhrases(content); // 실제 호출

		QuestionCoreDAO questionCoreDAO = QuestionCoreDAO.of(type, category, content, contentToken);
		QuestionCore unrelatedQuestionCore = QuestionCore.of(2L, "다른 질문");
		List<QuestionCore> questionCores = List.of(unrelatedQuestionCore);

		when(questionRepository.searchQuestions(questionCoreDAO)).thenReturn(Optional.of(questionCores));
		when(findMostSimilarQuestionId.invoke(questionCores, contentToken)).thenReturn(null);
		when(processAnswer.invoke(type, category, content, contentToken))
			.thenReturn(QuestionResponse.valueOfInvalidQuestion(content, "유사 질문이 없습니다."));

		// when
		QuestionResponse response = processQuestionService.invoke(type, category, content);

		// then
		verify(processAnswer, times(1)).invoke(type, category, content, contentToken);
		assertThat(response).isNotNull();
		assertThat(response.content()).isEqualTo(content);
		assertThat(response.answer().content()).isEqualTo("유사 질문이 없습니다.");
	}
}
