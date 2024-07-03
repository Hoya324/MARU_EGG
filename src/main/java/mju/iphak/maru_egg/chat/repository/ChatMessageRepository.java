package mju.iphak.maru_egg.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import mju.iphak.maru_egg.chat.domain.ChatMessage;
import mju.iphak.maru_egg.chat.dto.response.ChatMessageResponse;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageCustomRepository {
	Slice<ChatMessageResponse> findAllChatMessages(Long chatId, Long cursorId, Pageable pageable);
}
