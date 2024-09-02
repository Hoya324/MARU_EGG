package mju.iphak.maru_egg.question.docs;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mju.iphak.maru_egg.question.dto.request.UpdateQuestionTypeStatusRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionTypeStatusResponse;

@Tag(name = "Admin QuestionTypeStatus API", description = "어드민 질문타입 상태 관련 API 입니다.")
public interface AdminQuestionTypeStatusControllerDocs {

	@Operation(summary = "질문타입 초기화", description = "현재 제공 중인 질문 타입에 대해 DB에 초기화합니다.", responses = {
		@ApiResponse(responseCode = "200", description = "질문타입 초기화 성공")
	})
	void initializeQuestionTypeStatus();

	@Operation(summary = "질문타입 상태 변경", description = "질문타입의 상태를 변경합니다. (true <-> false)", responses = {
		@ApiResponse(responseCode = "200", description = "질문타입 상태 변경 성공")
	})
	void updateQuestionTypeStatus(@Valid @RequestBody UpdateQuestionTypeStatusRequest request);

	@Operation(summary = "전체 질문타입과 상태 조회", description = "전체 질문타입과 상태를 조회합니다.", responses = {
		@ApiResponse(responseCode = "200", description = "전체 질문타입과 상태 조회 성공")
	})
	List<QuestionTypeStatusResponse> getQuestionTypeStatus();
}
