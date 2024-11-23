package mju.iphak.maru_egg.admission.application.detail.command.update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import mju.iphak.maru_egg.admission.repository.AdmissionTypeDetailRepository;

class UpdateAdmissionTypeDetailServiceTest {

	@Mock
	private AdmissionTypeDetailRepository admissionTypeDetailRepository;

	@InjectMocks
	private UpdateAdmissionTypeDetailService updateAdmissionTypeDetail;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@DisplayName("상세 정보 업데이트 실패 - 존재하지 않는 ID")
	@Test
	void invoke_Failure_NotFound() {
		// given
		Long invalidId = 999L;
		when(admissionTypeDetailRepository.findById(invalidId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(EntityNotFoundException.class,
			() -> updateAdmissionTypeDetail.invoke(invalidId, "newName"));
	}
}