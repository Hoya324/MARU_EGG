package mju.iphak.maru_egg.chat.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.chat.application.ChatService;

@Tag(name = "chat", description = "채팅방 API")
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	// TODO: id 암호화
	// @Tag(name = "chat")
	// @Operation(summary = "채팅방 입장 및 채팅 내용 cursor 페이징", description = "채팅방에 입장하는 API", responses = {
	// 	@ApiResponse(responseCode = "200", description = "채팅방 입장 성공"),
	// 	@ApiResponse(responseCode = "404", description = "채팅방을 찾지 못한 경우"),
	// })
	// @GetMapping("/{id}")
	// public SliceResponse<ChatRoomResponse> entranceChat(
	// 	@PathVariable Long id,
	// 	@RequestParam(required = false) Long cursorId,
	// 	@RequestParam(defaultValue = "12") Integer size
	// ) {
	// 	return chatService.getChatOfCursorPaging(id, cursorId, size);
	// }

}
