package mju.iphak.maru_egg.admission.application;

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
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.domain.AdmissionTypeStatus;
import mju.iphak.maru_egg.admission.dto.response.AdmissionTypeStatusResponse;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;
import mju.iphak.maru_egg.common.MockTest;

class AdmissionTypeStatusServiceTest extends MockTest {

	@Mock
	private AdmissionTypeStatusRepository admissionTypeStatusRepository;

	@InjectMocks
	private AdmissionTypeStatusService admissionTypeStatusService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("질문 타입 상태 초기화 성공")
	@Test
	void initializeAdmissionTypeStatus_Success() {
		// given
		when(admissionTypeStatusRepository.count()).thenReturn(1L);

		// when
		admissionTypeStatusService.initializeAdmissionTypeStatus();

		// then
		verify(admissionTypeStatusRepository, times(1)).deleteAll();
		verify(admissionTypeStatusRepository, times(1)).saveAll(any());
	}

	@DisplayName("질문 타입 상태 업데이트 성공")
	@Test
	void updateStatus_Success() {
		// given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionTypeStatus status = new AdmissionTypeStatus(type, true);
		when(admissionTypeStatusRepository.findByAdmissionType(type)).thenReturn(Optional.of(status));

		// when
		admissionTypeStatusService.updateStatus(type);

		// then
		verify(admissionTypeStatusRepository, times(1)).findByAdmissionType(type);
		assertThat(status.isActivated()).isFalse();
	}

	@DisplayName("질문 타입 상태 업데이트 실패 - 타입 상태 없음")
	@Test
	void updateStatus_Failure_EntityNotFound() {
		// given
		AdmissionType type = AdmissionType.SUSI;
		when(admissionTypeStatusRepository.findByAdmissionType(type)).thenReturn(Optional.empty());

		// when & then
		EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
			() -> admissionTypeStatusService.updateStatus(type));
		assertThat(exception.getMessage()).isEqualTo(String.format(NOT_FOUND_ADMISSION_TYPE_STATUS.getMessage(), type));
	}

	@DisplayName("질문 타입 상태 목록 조회 성공")
	@Test
	void getAdmissionTypeStatus_Success() {
		// given
		AdmissionTypeStatus status1 = new AdmissionTypeStatus(AdmissionType.SUSI, true);
		AdmissionTypeStatus status2 = new AdmissionTypeStatus(AdmissionType.JEONGSI, false);
		when(admissionTypeStatusRepository.findAll()).thenReturn(List.of(status1, status2));

		// when
		List<AdmissionTypeStatusResponse> result = admissionTypeStatusService.getAdmissionTypeStatus();

		// then
		verify(admissionTypeStatusRepository, times(1)).findAll();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).type()).isEqualTo(AdmissionType.SUSI);
		assertThat(result.get(0).isActivated()).isTrue();
		assertThat(result.get(1).type()).isEqualTo(AdmissionType.JEONGSI);
		assertThat(result.get(1).isActivated()).isFalse();
	}

	@DisplayName("질문 타입 상태 삭제 성공")
	@Test
	void deleteAdmissionTypeStatus_Success() {
		// given
		AdmissionType type = AdmissionType.SUSI;

		// when
		admissionTypeStatusService.deleteAdmissionTypeStatus(type);

		// then
		verify(admissionTypeStatusRepository, times(1)).deleteByAdmissionType(eq(type));
	}
}