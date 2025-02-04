package mju.iphak.maru_egg.admission.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = "입학 전형", enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum AdmissionType {
	@JsonProperty("SUSI") SUSI("수시"),
	@JsonProperty("JEONGSI") JEONGSI("정시"),
	@JsonProperty("PYEONIP") PYEONIP("편입학");

	private final String type;

	@Override
	public String toString() {
		return this.type;
	}
}
