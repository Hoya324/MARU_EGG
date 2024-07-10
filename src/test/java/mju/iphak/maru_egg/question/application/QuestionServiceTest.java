package mju.iphak.maru_egg.question.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.dto.response.AnswerResponse;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

class QuestionServiceTest extends MockTest {

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private AnswerRepository answerRepository;

	@InjectMocks
	private QuestionService questionService;

	private Question question;
	private Answer answer;

	@BeforeEach
	void setUp() {
		question = Question.of("테스트 질문입니다.", QuestionType.JEONGSI, QuestionCategory.ADMISSION_GUIDELINE);
		answer = Answer.of(question, "테스트 답변입니다.");
	}

	@DisplayName("질문을 조회하는데 성공한 경우")
	@Test
	void 질문_조회_성공() {
		// given
		QuestionType type = QuestionType.JEONGSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
		String content = "테스트 질문입니다.";

		when(questionRepository.findByContentAndQuestionCategoryAndQuestionType(content, category, type))
			.thenReturn(Optional.of(question));

		when(answerRepository.findByQuestionId(question.getId()))
			.thenReturn(Optional.of(answer));

		// when
		QuestionResponse result = questionService.getQuestionResponse(type, category, content);

		// then
		assertThat(result).isEqualTo(QuestionResponse.of(question, AnswerResponse.from(answer)));
	}

	@DisplayName("질문을 조회하는데 실패한 경우-question을 못 찾은 경우")
	@Test
	void 질문_조회_실패_NOTFOUND() {
		// given
		QuestionType invalidType = QuestionType.SUSI;
		QuestionCategory category = QuestionCategory.ADMISSION_GUIDELINE;
		String invalidContent = "invalid 질문";

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			questionService.getQuestionResponse(invalidType, category, invalidContent);
		});

		// when & then
		assertThat(exception.getMessage()).isEqualTo(
			String.format(NOT_FOUND_QUESTION.getMessage(), invalidType, category, invalidContent));
	}
}