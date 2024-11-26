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

	@DisplayName("[실패] 답변 수정 요청 - 답변을 찾을 수 없음")
	@Test
	void 답변_수정_실패_답변_없음() {
		// given
		Long id = 1L;
		String newContent = "새로운 내용";
		given(answerRepository.findById(id)).willReturn(Optional.empty());

		// when
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
			() -> updateAnswerContent.invoke(id, newContent));

		// Then
		assertThat(exception.getMessage()).isEqualTo(String.format("답변 id가 %d인 답변을 찾을 수 없습니다.", id));
	}
}
