package mju.iphak.maru_egg.question.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.meta.CustomApiResponse;
import mju.iphak.maru_egg.common.meta.CustomApiResponses;
import mju.iphak.maru_egg.question.application.QuestionTypeStatusService;
import mju.iphak.maru_egg.question.dto.response.QuestionTypeStatusResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/questions/status")
public class QuestionTypeStatusController {

	private final QuestionTypeStatusService questionTypeStatusService;

	@CustomApiResponses({
		@CustomApiResponse(error = "InternalServerError", status = 500, message = "내부 서버 오류가 발생했습니다.", description = "내부 서버 오류")
	})
	@GetMapping()
	public List<QuestionTypeStatusResponse> getQuestionTypeStatus() {
		return questionTypeStatusService.getQuestionTypeStatus();
	}
}
