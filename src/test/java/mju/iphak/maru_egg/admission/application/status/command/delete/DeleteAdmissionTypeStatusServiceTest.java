package mju.iphak.maru_egg.admission.application.status.command.delete;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeStatusRepository;

class DeleteAdmissionTypeStatusServiceTest {

	@Mock
	private AdmissionTypeStatusRepository admissionTypeStatusRepository;

	@InjectMocks
	private DeleteAdmissionTypeStatusService deleteAdmissionTypeStatus;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("입학 전형 상태 삭제 성공")
	@Test
	void deleteAdmissionTypeStatus_Success() {
		// given
		AdmissionType type = AdmissionType.SUSI;

		// when
		deleteAdmissionTypeStatus.invoke(type);

		// then
		verify(admissionTypeStatusRepository, times(1)).deleteByAdmissionType(eq(type));
	}

}