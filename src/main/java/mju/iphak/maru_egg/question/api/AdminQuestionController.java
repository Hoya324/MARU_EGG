package mju.iphak.maru_egg.question.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.dto.request.CheckQuestionRequest;

@Tag(name = "Admin Question API", description = "질문 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/questions")
public class AdminQuestionController {

	private final QuestionService questionService;

	@Operation(summary = "질문-답변 체크", description = "질문-답변이 확인된 질문인지 체크하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 체크 성공")
	})
	@PostMapping("/check")
	public void checkQuestion(@Valid @RequestBody CheckQuestionRequest request) {
		questionService.checkQuestion(request.questionId(), request.check());
	}
}
