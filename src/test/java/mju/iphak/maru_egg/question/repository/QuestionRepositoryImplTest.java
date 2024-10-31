package mju.iphak.maru_egg.question.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mju.iphak.maru_egg.admission.domain.AdmissionCategory;
import mju.iphak.maru_egg.admission.domain.AdmissionType;
import mju.iphak.maru_egg.answer.domain.Answer;
import mju.iphak.maru_egg.answer.repository.AnswerRepository;
import mju.iphak.maru_egg.common.RepositoryTest;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.dao.request.SelectQuestionCores;
import mju.iphak.maru_egg.question.dao.request.SelectQuestions;
import mju.iphak.maru_egg.question.dao.response.QuestionCore;
import mju.iphak.maru_egg.question.domain.QQuestion;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

class QuestionRepositoryImplTest extends RepositoryTest {

	@Autowired
	private QuestionRepositoryImpl questionRepositoryImpl;

	@PersistenceContext
	EntityManager em;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private JPAQueryFactory queryFactory;

	private Question question;
	private Answer answer;

	@BeforeEach
	public void setUp() throws Exception {
		queryFactory = new JPAQueryFactory(em);

		String checkIndexQuery = "SHOW INDEX FROM questions WHERE Key_name = 'idx_ft_question_content'";
		List<?> result = jdbcTemplate.queryForList(checkIndexQuery);
		if (!result.isEmpty()) {
			jdbcTemplate.execute("ALTER TABLE questions DROP INDEX idx_ft_question_content");
		}

		jdbcTemplate.execute("ALTER TABLE questions ADD FULLTEXT INDEX idx_ft_question_content(content)");

		// 데이터 삽입
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, admission_type, admission_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			1L, "수시 입학 요강에 대해 알려주세요.", "수시 입학 요강 대해", AdmissionType.SUSI.name(),
			AdmissionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, admission_type, admission_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			2L, "수시 시험 일정 알려줘", "수시 시험 일정", AdmissionType.SUSI.name(), AdmissionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, admission_type, admission_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			3L, "수시 시기 알려줘", "수시 시기", AdmissionType.SUSI.name(), AdmissionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, admission_type, admission_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			4L, "수시 면접 일정 알려줘", "수시 면접 일정", AdmissionType.SUSI.name(), AdmissionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, admission_type, admission_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			5L, "수시", "수시", AdmissionType.SUSI.name(), AdmissionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, admission_type, admission_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			6L, "2024 정시 입학 요강에 대해 알려주세요.", "정시 입학 요강 대해", AdmissionType.SUSI.name(),
			AdmissionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, admission_type, admission_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			7L, "시기 알려줘", "시기", AdmissionType.SUSI.name(), AdmissionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, admission_type, admission_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			8L, "2024 장시 입학 요강에 대해 알려주세요.", "장시 입학 요강 대해", AdmissionType.SUSI.name(),
			AdmissionCategory.ADMISSION_GUIDELINE.name(), 0);
		jdbcTemplate.update(
			"INSERT INTO questions (id, content, content_token, admission_type, admission_category, view_count) VALUES (?, ?, ?, ?, ?, ?)",
			9L, "2024 수시 입학 요강에 대해 알려주세요.", "2024 수시 입학 요강 대해", AdmissionType.SUSI.name(),
			AdmissionCategory.ADMISSION_GUIDELINE.name(), 0);

		question = questionRepository.findById(1L).orElseThrow();

		answer = Answer.of(question, "테스트 답변입니다.");
		answerRepository.save(answer);
	}

	@Test
	void contentToken_type_질문_검색_성공() {
		// given
		QQuestion question = QQuestion.question;
		String contentToken = "수시 입학 요강 대해";
		AdmissionType type = AdmissionType.SUSI;
		NumberTemplate<Double> numberTemplate = createBooleanTemplateByContentToken(question, contentToken);

		// when
		List<Question> results = queryFactory.selectFrom(question)
			.where(numberTemplate.gt(0.0).or(numberTemplate.eq(0.0)).and(question.admissionType.eq(type)))
			.fetch();

		// then
		assertThat(results).isNotEmpty();
		assertThat(results.get(0).getContent()).contains("수시 입학 요강");
	}

	@DisplayName("contentToken, type으로 질문을 검색하는데 실패한 경우")
	@Test
	void contentToken_type으로_질문_검색_실패() {
		// given
		String invalidContentToken = "잘못된 질문";
		SelectQuestionCores selectQuestionCores = SelectQuestionCores.of(AdmissionType.SUSI, null, invalidContentToken,
			invalidContentToken);

		// when
		Optional<List<QuestionCore>> result = questionRepositoryImpl.searchQuestions(selectQuestionCores);

		// then
		assertThat(result).isPresent();
		assertThat(result.get()).isEmpty();
	}

	@DisplayName("contentToken, type, Category로 질문을 검색하는데 성공")
	@Test
	void contentToken_type_Category로_질문_검색_성공() {
		// given
		QQuestion question = QQuestion.question;
		String contentToken = "수시 입학 요강 대해";
		AdmissionType type = AdmissionType.SUSI;
		AdmissionCategory category = AdmissionCategory.ADMISSION_GUIDELINE;
		NumberTemplate<Double> numberTemplate = createBooleanTemplateByContentToken(question, contentToken);

		// when
		List<String> results = queryFactory.select(question.contentToken)
			.from(question)
			.where(numberTemplate.gt(0.0)
				.or(numberTemplate.eq(0.0))
				.and(question.admissionType.eq(type))
				.and(question.admissionCategory.eq(category)))
			.fetch();

		// then
		assertThat(results).isNotEmpty();
	}

	@DisplayName("contentToken, type, Category로 질문을 검색하는데 실패한 경우")
	@Test
	void contentToken_type_Category로_질문_검색_실패() {
		// given
		String invalidContentToken = "잘못된 질문";
		SelectQuestionCores selectQuestionCores = SelectQuestionCores.of(AdmissionType.SUSI,
			AdmissionCategory.ADMISSION_GUIDELINE, invalidContentToken,
			invalidContentToken);

		// when
		Optional<List<QuestionCore>> result = questionRepositoryImpl.searchQuestions(selectQuestionCores);

		// then
		assertThat(result).isPresent();
		assertThat(result.get()).isEmpty();
	}

	@DisplayName("content로 질문을 페이지네이션 조회 - 실패")
	@Test
	void content로_질문_페이지네이션_조회_실패() {
		// given
		String content = "존재하지 않는 질문";
		Pageable pageable = PageRequest.of(0, 3);
		SelectQuestions selectQuestions = SelectQuestions.of(AdmissionType.SUSI, AdmissionCategory.ADMISSION_GUIDELINE,
			content, null, null, pageable);

		// when
		SliceQuestionResponse<SearchedQuestionsResponse> result = questionRepositoryImpl.searchQuestionsOfCursorPaging(
			selectQuestions);

		// then
		assertThat(result.data()).isEmpty();
	}

	@Test
	void contentToken_type() {
		QQuestion question = QQuestion.question;
		String contentToken = "수시 입학 요강 대해";
		AdmissionType type = AdmissionType.SUSI;
		NumberTemplate<Double> numberTemplate = createBooleanTemplateByContentToken(question, contentToken);

		List<String> results = queryFactory.select(question.contentToken)
			.from(question)
			.where(numberTemplate.gt(0.0).or(numberTemplate.eq(0.0)).and(question.admissionType.eq(type)))
			.fetch();

		assertThat(results).isNotEmpty();
	}

	@DisplayName("Full Text Search를 사용하여 content로 질문 검색 - 성공")
	@Test
	void fullTextSearch_content_질문_검색_성공() {
		// given
		QQuestion question = QQuestion.question;
		String contentToken = "수시 입학 요강 대해";

		NumberTemplate<Double> numberTemplate = createBooleanTemplateByContentToken(question, contentToken);

		// when
		List<Question> results = queryFactory.selectFrom(question)
			.where(numberTemplate.gt(0.0).or(numberTemplate.eq(0.0)))
			.fetch();

		// then
		assertThat(results.get(0).getContentToken()).contains(contentToken);
	}

	@DisplayName("Full Text Search를 사용하여 content로 질문 검색 - 실패")
	@Test
	void fullTextSearch_content_질문_검색_실패() {
		// given
		QQuestion question = QQuestion.question;
		String invalidContentToken = "존재하지 않는 질문";
		NumberTemplate<Double> numberTemplate = createBooleanTemplateByContentToken(question, invalidContentToken);

		// when
		List<Question> results = queryFactory.selectFrom(question)
			.where(numberTemplate.gt(0.0))
			.fetch();

		// then
		assertThat(results).isEmpty();
	}

	private NumberTemplate<Double> createBooleanTemplateByContentToken(QQuestion question, String content) {
		return Expressions.numberTemplate(Double.class, "function('match', {0}, {1})", question.content, content);
	}
}