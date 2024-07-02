package mju.iphak.maru_egg.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅 메세지 기본 조회 응답 DTO")
public record ChatMessageResponse(

	@Schema(description = "채팅 메세지 내용")
	String content
) {
	public static ChatMessageResponse from(String content) {
		return new ChatMessageResponse(content);
	}
}
