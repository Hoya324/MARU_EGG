package mju.iphak.maru_egg.question.application.check;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.common.exception.ErrorCode;
import mju.iphak.maru_egg.question.application.command.check.CheckQuestionService;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

class CheckQuestionServiceTest extends MockTest {

	@Mock
	private QuestionRepository questionRepository;

	@InjectMocks
	private CheckQuestionService checkQuestionService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("[성공] Question ID가 존재할 때 isChecked가 업데이트된다")
	@Test
	void 질문_ID_존재() {
		// given
		Long questionId = 1L;
		Question mockQuestion = mock(Question.class);

		given(questionRepository.findById(questionId)).willReturn(java.util.Optional.of(mockQuestion));

		// when
		checkQuestionService.invoke(questionId);

		// then
		verify(mockQuestion, times(1)).updateIsChecked();
	}

	@DisplayName("[실패] 존재하지 않는 Question ID를 호출했을 때 EntityNotFoundException 발생")
	@Test
	void 질문_ID_존재하지_않음() {
		// given
		Long questionId = 1L;

		given(questionRepository.findById(questionId)).willReturn(java.util.Optional.empty());

		// when & then
		assertThatThrownBy(() -> checkQuestionService.invoke(questionId))
			.isInstanceOf(EntityNotFoundException.class)
			.hasMessage(String.format(ErrorCode.NOT_FOUND_QUESTION_BY_ID.getMessage(), questionId));

		// then
		verify(questionRepository, times(1)).findById(questionId);
	}
}
