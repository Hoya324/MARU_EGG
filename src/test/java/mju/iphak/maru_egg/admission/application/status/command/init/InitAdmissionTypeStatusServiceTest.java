package mju.iphak.maru_egg.admission.application.status.command.init;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;
import mju.iphak.maru_egg.common.MockTest;

class InitAdmissionTypeStatusServiceTest extends MockTest {

	@Mock
	private AdmissionTypeStatusRepository admissionTypeStatusRepository;

	@InjectMocks
	private InitAdmissionTypeStatusService initAdmissionTypeStatusService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("입학 전형 상태 초기화 성공")
	@Test
	void invoke_Success() {
		// given
		when(admissionTypeStatusRepository.count()).thenReturn(1L);

		// when
		initAdmissionTypeStatusService.invoke();

		// then
		verify(admissionTypeStatusRepository, times(1)).deleteAll();
		verify(admissionTypeStatusRepository, times(1)).saveAll(any());
	}

}