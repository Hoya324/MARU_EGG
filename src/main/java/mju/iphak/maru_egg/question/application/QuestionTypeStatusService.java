package mju.iphak.maru_egg.question.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.domain.QuestionTypeStatus;
import mju.iphak.maru_egg.question.dto.response.QuestionTypeStatusResponse;
import mju.iphak.maru_egg.question.repository.QuestionTypeStatusRepository;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class QuestionTypeStatusService {

	private final QuestionTypeStatusRepository questionTypeStatusRepository;

	public void initializeQuestionTypeStatus() {
		List<QuestionTypeStatus> questionTypeStatuses = QuestionTypeStatus.initialize();
		if (!isDatabaseEmpty()) {
			questionTypeStatusRepository.deleteAll();
		}
		questionTypeStatusRepository.saveAll(questionTypeStatuses);
	}

	public void updateStatus(final QuestionType type) {
		QuestionTypeStatus questionTypeStatus = questionTypeStatusRepository.findByQuestionType(type)
			.orElseThrow(() -> new EntityNotFoundException(
				String.format(NOT_FOUND_QUESTION_TYPE_STATUS.getMessage(), type)));
		questionTypeStatus.updateStatus();
	}

	public List<QuestionTypeStatusResponse> getQuestionTypeStatus() {
		List<QuestionTypeStatus> questionTypeStatuses = questionTypeStatusRepository.findAll();
		return questionTypeStatuses.stream()
			.map(questionTypeStatus -> QuestionTypeStatusResponse.of(questionTypeStatus.getQuestionType(),
				questionTypeStatus.isActivated()))
			.toList();
	}

	public void deleteQuestionTypeStatus(QuestionType type) {
		questionTypeStatusRepository.deleteByQuestionType(type);
	}

	private boolean isDatabaseEmpty() {
		return questionTypeStatusRepository.count() == 0;
	}
}
