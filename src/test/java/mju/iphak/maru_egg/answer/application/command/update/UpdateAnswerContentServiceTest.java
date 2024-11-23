package mju.iphak.maru_egg.answer.application.command.update;

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
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.MockTest;

class UpdateAnswerContentServiceTest extends MockTest {

	@Mock
	private AnswerRepository answerRepository;

	@InjectMocks
	private UpdateAnswerContentService updateAnswerContent;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("[실패] 답변 내용을 수정할 때, 답변을 찾을 수 없으면 예외가 발생한다.")
	@Test
	void 답변_내용_수정_실패_NOTFOUND() {
		// given
		Long id = 1L;
		given(answerRepository.findById(anyLong())).willReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
			updateAnswerContent.invoke(id, "새로운 내용");
		});

		assertThat(exception.getMessage()).isEqualTo("답변 id가 1인 답변을 찾을 수 없습니다.");
	}
}
