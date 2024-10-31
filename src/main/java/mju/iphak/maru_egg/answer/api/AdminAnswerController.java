package mju.iphak.maru_egg.answer.api;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.answer.api.swagger.AdminAnswerControllerDocs;
import mju.iphak.maru_egg.answer.application.AnswerManager;
import mju.iphak.maru_egg.answer.dto.request.UpdateAnswerContentRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/answers")
public class AdminAnswerController implements AdminAnswerControllerDocs {

	private final AnswerManager answerManager;

	@PutMapping()
	public void updateAnswerContent(@Valid @RequestBody UpdateAnswerContentRequest request) {
		answerManager.updateAnswerContent(request.id(), request.content());
	}
}
