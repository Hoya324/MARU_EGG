package mju.iphak.maru_egg.question.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.meta.CustomApiResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponses;
import mju.iphak.maru_egg.question.application.QuestionTypeStatusService;
import mju.iphak.maru_egg.question.docs.AdminQuestionTypeStatusControllerDocs;
import mju.iphak.maru_egg.question.dto.request.UpdateQuestionTypeStatusRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/questions/status")
public class AdminQuestionTypeStatusController implements AdminQuestionTypeStatusControllerDocs {

	private final QuestionTypeStatusService questionTypeStatusService;

	@CustomApiResponses({
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@PostMapping("/init")
	public void initializeQuestionTypeStatus() {
		questionTypeStatusService.initializeQuestionTypeStatus();
	}

	@CustomApiResponses({
		@CustomApiResponse(error = "HttpMessageNotReadableException", status = 400, message = "\"Invalid input format: JSON parse error: Cannot deserialize value of type `java.lang.Long` from String \\\"잘못된 형식의 질문 ID\\\": not a valid `java.lang.Long` value\"", description = "json 형식 및 타입에 맞지 않은 요청을 할 경우"),
		@CustomApiResponse(error = "EntityNotFoundException", status = 404, message = "질문 타입이 수시인 질문 상태를 찾을 수 없습니다", description = "해당 타입을 찾을 수 없는 경우"),
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@PutMapping()
	public void updateQuestionTypeStatus(@Valid @RequestBody UpdateQuestionTypeStatusRequest request) {
		questionTypeStatusService.updateStatus(request.type());
	}
}
