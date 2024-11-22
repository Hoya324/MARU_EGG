package mju.iphak.maru_egg.question.application.find;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.application.find.FindAnswerByQuestionIdService;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.common.MockTest;
import mju.iphak.maru_egg.question.dao.request.QuestionCoreDAO;
import mju.iphak.maru_egg.question.dao.response.QuestionCore;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.response.QuestionListItemResponse;
import mju.iphak.maru_egg.question.repository.QuestionRepository;

class FindAllQuestionsServiceTest extends MockTest {

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private FindAnswerByQuestionIdService findAnswerByQuestionId;

	@InjectMocks
	private FindAllQuestionsService findAllQuestionsService;

	private Question question;
	private Answer answer;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		question = mock(Question.class);
		answer = mock(Answer.class);

		when(question.getId()).thenReturn(1L);
		when(answer.getId()).thenReturn(1L);
		when(findAnswerByQuestionId.invoke(1L)).thenReturn(answer);
		when(questionRepository.searchQuestions(any(QuestionCoreDAO.class)))
			.thenReturn(Optional.of(List.of(QuestionCore.of(1L, "테스트 질문입니다."))));
		when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
		findAllQuestionsService = new FindAllQuestionsService(questionRepository, findAnswerByQuestionId);
	}

	@DisplayName("질문 목록을 조회하는데 성공한 경우")
	@Test
	void 질문_목록_조회_성공() {
		// given
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;

		when(questionRepository.findAllByAdmissionTypeAndAdmissionCategoryOrderByViewCountDesc(type, category))
			.thenReturn(List.of(question));
		when(findAnswerByQuestionId.invoke(question.getId())).thenReturn(answer);

		// when
		List<QuestionListItemResponse> result = findAllQuestionsService.invoke(type, category);

		// then
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertThat(result.get(0).content()).isEqualTo(question.getContent());
		assertThat(result.get(0).answer().content()).isEqualTo(answer.getContent());
	}

	@DisplayName("질문 목록을 조회하는데 성공한 경우 - 카테고리 없이 타입으로 조회")
	@Test
	void 질문_목록_조회_성공_카테고리_없이() {
		// given
		AdmissionType type = AdmissionType.SUSI;

		when(questionRepository.findAllByAdmissionTypeOrderByViewCountDesc(type))
			.thenReturn(List.of(question));
		when(findAnswerByQuestionId.invoke(question.getId())).thenReturn(answer);

		// when
		List<QuestionListItemResponse> result = findAllQuestionsService.invoke(type, null);

		// then
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertThat(result.get(0).content()).isEqualTo(question.getContent());
		assertThat(result.get(0).answer().content()).isEqualTo(answer.getContent());
	}

}