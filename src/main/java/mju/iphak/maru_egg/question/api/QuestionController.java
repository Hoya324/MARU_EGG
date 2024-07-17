package mju.iphak.maru_egg.question.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import mju.iphak.maru_egg.question.dto.request.FindQuestionsRequest;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

@Tag(name = "Question API", description = "질문 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

	private final QuestionService questionService;

	@Operation(summary = "질문 요청", description = "질문하는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 성공"),
		@ApiResponse(responseCode = "404", description = "질문 또는 답변을 찾지 못한 경우"),
		@ApiResponse(responseCode = "500", description = "내부 서버 오류")
	})
	@PostMapping()
	public QuestionResponse question(@Valid @RequestBody QuestionRequest request) {
		return questionService.question(request.type(), request.category(), request.content());
	}

	@Operation(summary = "질문 목록 요청", description = "질문 목록을 보내주는 API", responses = {
		@ApiResponse(responseCode = "200", description = "질문 성공"),
		@ApiResponse(responseCode = "404", description = "질문 또는 답변을 찾지 못한 경우"),
		@ApiResponse(responseCode = "500", description = "내부 서버 오류")
	})
	@GetMapping()
	public List<QuestionResponse> getQuestions(@Valid @ModelAttribute FindQuestionsRequest request) {
		return questionService.getQuestions(request.type(), request.category());
	}
}