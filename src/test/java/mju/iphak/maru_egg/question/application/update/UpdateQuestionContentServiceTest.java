package mju.iphak.maru_egg.question.application.update;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

class UpdateQuestionContentServiceTest extends MockTest {

	@Mock
	private QuestionRepository questionRepository;

	@InjectMocks
	private UpdateQuestionContentService updateQuestionContentService;

	private Question question;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		question = mock(Question.class);
	}

	@DisplayName("[실패] 질문 내용 수정 실패 - 질문이 존재하지 않는 경우")
	@Test
	void 질문_내용_수정_실패_NOTFOUND() {
		// given
		Long invalidId = 1L;
		given(questionRepository.findById(invalidId)).willReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			updateQuestionContentService.invoke(invalidId, "새로운 내용");
		});

		// then
		assertThat(exception.getMessage()).isEqualTo("id: 1인 질문을 찾을 수 없습니다.");
		verify(questionRepository, times(1)).findById(invalidId);
	}

	@DisplayName("[성공] 유효한 질문 ID로 내용 수정 성공")
	@Test
	void 질문_내용_수정_성공() {
		// given
		Long validId = 1L;
		String updatedContent = "새로운 내용";
		when(questionRepository.findById(validId)).thenReturn(Optional.of(question));

		// when
		updateQuestionContentService.invoke(validId, updatedContent);

		// then
		assertThat(question.getContent()).isEqualTo(updatedContent);
		verify(questionRepository, times(1)).findById(validId);
	}
}
