package mju.iphak.maru_egg.question.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.question.api.swagger.AdminQuestionControllerDocs;
import mju.iphak.maru_egg.question.application.QuestionService;
import mju.iphak.maru_egg.question.dto.request.CheckQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.CreateQuestionRequest;
import mju.iphak.maru_egg.question.dto.request.UpdateQuestionContentRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController implements AdminQuestionControllerDocs {

	private final QuestionService questionService;

	@PutMapping()
	public void updateQuestionContent(@Valid @RequestBody UpdateQuestionContentRequest request) {
		updateQuestionContent.invoke(request.id(), request.content());
	}

	@PutMapping("/check")
	public void checkQuestion(@Valid @RequestBody CheckQuestionRequest request) {
		checkQuestion.invoke(request.questionId());
	}

	@PostMapping("/new")
	public void createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
		createCustomQuestion.invoke(request);
	}

	@DeleteMapping("/{questionId}")
	public void deleteQuestion(@PathVariable("questionId") Long id) {
		questionService.deleteQuestion(id);
	}
}
