package mju.iphak.maru_egg.common.dto.pagination;

import java.util.List;

public record SliceQuestionResponse<T>(
	List<T> data,
	int currentPage,
	int pageSize,
	boolean hasNext,
	Integer nextCursorViewCount,
	Long nextQuestionId
) {
	public SliceQuestionResponse(List<T> data, int currentPage, int pageSize, boolean hasNext) {
		this(data, currentPage, pageSize, hasNext, null, null);
	}
}