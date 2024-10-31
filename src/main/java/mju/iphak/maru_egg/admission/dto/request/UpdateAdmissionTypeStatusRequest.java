package mju.iphak.maru_egg.admission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import mju.iphak.maru_egg.admission.domain.AdmissionType;

@Schema(description = "질문 목록 요청 DTO")
public record UpdateAdmissionTypeStatusRequest(

	@Schema(description = "질문 타입(수시, 정시, 편입학)", allowableValues = {"SUSI", "JEONGSI", "PYEONIP"})
	AdmissionType type
) {
}