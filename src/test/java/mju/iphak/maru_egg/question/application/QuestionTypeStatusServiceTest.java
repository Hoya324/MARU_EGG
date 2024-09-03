package mju.iphak.maru_egg.question.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.domain.QuestionTypeStatus;
import mju.iphak.maru_egg.question.dto.response.QuestionTypeStatusResponse;
import mju.iphak.maru_egg.question.repository.QuestionTypeStatusRepository;

class QuestionTypeStatusServiceTest extends MockTest {

	@Mock
	private QuestionTypeStatusRepository questionTypeStatusRepository;

	@InjectMocks
	private QuestionTypeStatusService questionTypeStatusService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("질문 타입 상태 초기화 성공")
	@Test
	void initializeQuestionTypeStatus_Success() {
		// given
		when(questionTypeStatusRepository.count()).thenReturn(1L);

		// when
		questionTypeStatusService.initializeQuestionTypeStatus();

		// then
		verify(questionTypeStatusRepository, times(1)).deleteAll();
		verify(questionTypeStatusRepository, times(1)).saveAll(any());
	}

	@DisplayName("질문 타입 상태 업데이트 성공")
	@Test
	void updateStatus_Success() {
		// given
		QuestionType type = QuestionType.SUSI;
		QuestionTypeStatus status = new QuestionTypeStatus(type, true);
		when(questionTypeStatusRepository.findByQuestionType(type)).thenReturn(Optional.of(status));

		// when
		questionTypeStatusService.updateStatus(type);

		// then
		verify(questionTypeStatusRepository, times(1)).findByQuestionType(type);
		assertThat(status.isActivated()).isFalse();
	}

	@DisplayName("질문 타입 상태 업데이트 실패 - 타입 상태 없음")
	@Test
	void updateStatus_Failure_EntityNotFound() {
		// given
		QuestionType type = QuestionType.SUSI;
		when(questionTypeStatusRepository.findByQuestionType(type)).thenReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
			() -> questionTypeStatusService.updateStatus(type));
		assertThat(exception.getMessage()).isEqualTo(String.format(NOT_FOUND_QUESTION_TYPE_STATUS.getMessage(), type));
	}

	@DisplayName("질문 타입 상태 목록 조회 성공")
	@Test
	void getQuestionTypeStatus_Success() {
		// given
		QuestionTypeStatus status1 = new QuestionTypeStatus(QuestionType.SUSI, true);
		QuestionTypeStatus status2 = new QuestionTypeStatus(QuestionType.JEONGSI, false);
		when(questionTypeStatusRepository.findAll()).thenReturn(List.of(status1, status2));

		// when
		List<QuestionTypeStatusResponse> result = questionTypeStatusService.getQuestionTypeStatus();

		// then
		verify(questionTypeStatusRepository, times(1)).findAll();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).type()).isEqualTo(QuestionType.SUSI);
		assertThat(result.get(0).isActivated()).isTrue();
		assertThat(result.get(1).type()).isEqualTo(QuestionType.JEONGSI);
		assertThat(result.get(1).isActivated()).isFalse();
	}

	@DisplayName("질문 타입 상태 삭제 성공")
	@Test
	void deleteQuestionTypeStatus_Success() {
		// given
		QuestionType type = QuestionType.SUSI;

		// when
		questionTypeStatusService.deleteQuestionTypeStatus(type);

		// then
		verify(questionTypeStatusRepository, times(1)).deleteByQuestionType(eq(type));
	}
}