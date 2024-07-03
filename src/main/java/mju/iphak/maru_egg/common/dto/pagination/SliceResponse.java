package mju.iphak.maru_egg.common.dto.pagination;

import java.util.List;

public record SliceResponse<T>(
	List<T> data,
	int currentPage,
	int pageSize,
	boolean hasNext
) {
	public SliceResponse(final List<T> data, final int currentPage, final int pageSize, final boolean hasNext) {
		this.data = data;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.hasNext = hasNext;
	}
}
