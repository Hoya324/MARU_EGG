package mju.iphak.maru_egg.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import mju.iphak.maru_egg.chat.dto.response.ChatMessageResponse;

public interface ChatMessageCustomRepository {

	Slice<ChatMessageResponse> findAllChatMessages(Long chatId, Long cursorId, Pageable pageable);
}
