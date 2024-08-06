package mju.iphak.maru_egg.question.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.docs.AdminQuestionControllerDocs;
import mju.iphak.maru_egg.question.dto.request.CheckQuestionRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController implements AdminQuestionControllerDocs {

	private final QuestionService questionService;

	@PostMapping("/check")
	public void checkQuestion(@Valid @RequestBody CheckQuestionRequest request) {
		questionService.checkQuestion(request.questionId(), request.check());
	}
}
