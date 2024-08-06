package mju.iphak.maru_egg.answer.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.answer.application.AnswerService;
import mju.iphak.maru_egg.answer.docs.AdminAnswerControllerDocs;
import mju.iphak.maru_egg.answer.dto.request.UpdateAnswerContentRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/answers")
public class AdminAnswerController implements AdminAnswerControllerDocs {

	private final AnswerService answerService;

	@PostMapping()
	public void updateAnswerContent(@Valid @RequestBody UpdateAnswerContentRequest request) {
		answerService.updateAnswerContent(request.id(), request.content());
	}
}
