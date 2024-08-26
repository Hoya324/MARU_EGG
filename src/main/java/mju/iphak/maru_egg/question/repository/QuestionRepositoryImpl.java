package mju.iphak.maru_egg.question.repository;

import static mju.iphak.maru_egg.question.domain.QQuestion.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.common.dto.pagination.SliceQuestionResponse;
import mju.iphak.maru_egg.question.domain.Question;
import mju.iphak.maru_egg.question.domain.QuestionCategory;
import mju.iphak.maru_egg.question.domain.QuestionType;
import mju.iphak.maru_egg.question.dto.response.QuestionCore;
import mju.iphak.maru_egg.question.dto.response.SearchedQuestionsResponse;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

	private static final double MIN_MATCHING_POINT = 0.005;

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<List<QuestionCore>> searchQuestionsByContentTokenAndType(final String contentToken,
		final QuestionType type) {
		NumberTemplate<Double> booleanTemplate = createBooleanTemplate(contentToken);
		List<Tuple> tuples = queryFactory.select(question.id, question.contentToken)
			.from(question)
			.where(booleanTemplate.gt(0).and(question.questionType.eq(type)))
			.fetch();

		List<QuestionCore> result = tuples.stream()
			.map(tuple -> QuestionCore.of(tuple.get(question.id), tuple.get(question.contentToken)))
			.collect(Collectors.toList());

		return Optional.of(result);
	}

	@Override
	public Optional<List<QuestionCore>> searchQuestionsByContentTokenAndTypeAndCategory(final String contentToken,
		final QuestionType type, final QuestionCategory category) {
		NumberTemplate<Double> booleanTemplate = createBooleanTemplate(contentToken);
		List<Tuple> tuples = queryFactory.select(question.id, question.contentToken)
			.from(question)
			.where(booleanTemplate.gt(0)
				.and(question.questionType.eq(type))
				.and(question.questionCategory.eq(category)))
			.fetch();

		List<QuestionCore> result = tuples.stream()
			.map(tuple -> QuestionCore.of(tuple.get(question.id), tuple.get(question.contentToken)))
			.collect(Collectors.toList());

		return Optional.of(result);
	}

	@Override
	public SliceQuestionResponse<SearchedQuestionsResponse> searchQuestionsOfCursorPagingByContentWithFullTextSearch(
		final QuestionType type, final QuestionCategory category, final String content,
		final Integer cursorViewCount, final Long questionId, final Pageable pageable) {

		int pageSize = pageable.getPageSize();
		Integer viewCountCursorKey = getValidCursorViewCount(cursorViewCount);
		Long questionIdCursorKey = getValidCursorQuestionId(questionId);

		NumberTemplate<Double> booleanTemplate = createBooleanTemplate(content);
		BooleanExpression cursorPredicate = createCursorPredicate(viewCountCursorKey, questionIdCursorKey);

		List<Question> questions = fetchQuestions(type, category, booleanTemplate, cursorPredicate, pageSize);

		return buildSliceQuestionResponse(pageable, pageSize, questions);
	}

	@Override
	public SliceQuestionResponse<SearchedQuestionsResponse> searchQuestionsOfCursorPagingByContentWithLikeFunction(
		final QuestionType type, final QuestionCategory category, final String content,
		final Integer cursorViewCount, final Long questionId, final Pageable pageable) {

		int pageSize = pageable.getPageSize();

		BooleanBuilder whereClause = buildWhereClause(type, category, content);
		List<Question> questions = queryFactory.selectFrom(question)
			.where(whereClause)
			.limit(pageSize + 1)
			.fetch();

		return buildSliceQuestionResponse(pageable, pageSize, questions);
	}

	private NumberTemplate<Double> createBooleanTemplate(String content) {
		return Expressions.numberTemplate(Double.class, "function('match', {0}, {1})", question.content, content);
	}

	private BooleanExpression createCursorPredicate(Integer cursorViewCount, Long questionId) {
		if (cursorViewCount != null && questionId != null) {
			return question.viewCount.lt(cursorViewCount)
				.or(question.viewCount.eq(cursorViewCount).and(question.id.lt(questionId)));
		} else if (cursorViewCount != null) {
			return question.viewCount.lt(cursorViewCount);
		} else if (questionId != null) {
			return question.id.lt(questionId);
		}
		return null;
	}

	private Integer getValidCursorViewCount(Integer cursorViewCount) {
		return (cursorViewCount != null && cursorViewCount > 0) ? cursorViewCount : Integer.MAX_VALUE;
	}

	private Long getValidCursorQuestionId(Long questionId) {
		return (questionId != null && questionId > 0) ? questionId : Long.MAX_VALUE;
	}

	private List<Question> fetchQuestions(
		final QuestionType type, final QuestionCategory category,
		NumberTemplate<Double> booleanTemplate, BooleanExpression cursorPredicate, int pageSize) {

		BooleanBuilder whereClause = new BooleanBuilder();
		whereClause.and(booleanTemplate.gt(MIN_MATCHING_POINT));

		if (cursorPredicate != null) {
			whereClause.and(cursorPredicate);
		}

		if (type != null) {
			whereClause.and(question.questionType.eq(type));
		}

		if (category != null) {
			whereClause.and(question.questionCategory.eq(category));
		}

		return queryFactory.selectFrom(question)
			.where(whereClause)
			.orderBy(question.viewCount.desc(), question.id.desc())
			.limit(pageSize + 1)
			.fetch();
	}

	private BooleanBuilder buildWhereClause(final QuestionType type, final QuestionCategory category,
		final String content) {
		BooleanBuilder whereClause = new BooleanBuilder();
		if (content != null && !content.isEmpty()) {
			whereClause.and(question.content.contains(content));
		}
		if (type != null) {
			whereClause.and(question.questionType.eq(type));
		}
		if (category != null) {
			whereClause.and(question.questionCategory.eq(category));
		}
		return whereClause;
	}

	@NotNull
	private SliceQuestionResponse<SearchedQuestionsResponse> buildSliceQuestionResponse(
		final Pageable pageable, final int pageSize, final List<Question> questions) {

		boolean hasNext = questions.size() > pageSize;
		if (hasNext) {
			questions.remove(pageSize);
		}

		List<SearchedQuestionsResponse> questionResponses = questions.stream()
			.map(q -> SearchedQuestionsResponse.
				of(q.getId(), q.getContent(), q.isChecked()))
			.collect(Collectors.toList());

		Integer nextCursorViewCount = null;
		Long nextQuestionId = null;
		if (!questions.isEmpty()) {
			Question lastQuestion = questions.get(questions.size() - 1);
			nextCursorViewCount = lastQuestion.getViewCount();
			nextQuestionId = lastQuestion.getId();
		}

		return new SliceQuestionResponse<>(questionResponses, pageable.getPageNumber(), pageSize, hasNext,
			nextCursorViewCount, nextQuestionId);
	}
}