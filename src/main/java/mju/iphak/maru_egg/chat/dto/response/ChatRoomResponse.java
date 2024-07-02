package mju.iphak.maru_egg.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import mju.iphak.maru_egg.common.dto.pagination.SliceResponse;

@Schema(description = "채팅방 기본 조회 응답 DTO")
public record ChatRoomResponse(

	@Schema(description = "채팅방 id")
	Long id,

	@Schema(description = "채팅 메세지")
	SliceResponse<ChatMessageResponse> chatMessages
) {
	public static ChatRoomResponse of(Long id, SliceResponse<ChatMessageResponse> chatMessages) {
		return new ChatRoomResponse(id, chatMessages);
	}
}
