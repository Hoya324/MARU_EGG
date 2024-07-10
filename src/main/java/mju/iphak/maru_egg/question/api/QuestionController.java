package mju.iphak.maru_egg.question.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.dto.request.QuestionRequest;
import mju.iphak.maru_egg.question.dto.response.QuestionResponse;

@Tag(name = "Question API", description = "질문 관련 API 입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

	private final QuestionService questionService;

	@PostMapping()
	public QuestionResponse question(@RequestBody QuestionRequest request) {
		return questionService.getQuestionResponse(request.type(), request.category(), request.content());
	}
}
