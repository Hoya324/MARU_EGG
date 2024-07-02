package mju.iphak.maru_egg.chat.application;

import static mju.iphak.maru_egg.common.exception.ErrorCode.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.chat.domain.ChatRoom;
import mju.iphak.maru_egg.chat.dto.response.ChatMessageResponse;
import mju.iphak.maru_egg.chat.dto.response.ChatRoomResponse;
import mju.iphak.maru_egg.chat.repository.ChatMessageRepository;
import mju.iphak.maru_egg.chat.repository.ChatRoomRepository;
import mju.iphak.maru_egg.common.dto.pagination.SliceResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

	private final ChatRoomRepository chatRoomRepository;
	private final ChatMessageRepository chatMessageRepository;

	public void save(ChatRoom chatRoom) {
	}

	@Transactional(readOnly = true)
	public ChatRoomResponse getChatOfCursorPaging(Long id, Long cursorId, Integer size) {
		chatRoomRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(String.format(NOT_FOUND_CHAT.getMessage(), id)));

		Pageable pageable = PageRequest.of(0, size);
		Slice<ChatMessageResponse> slice = chatMessageRepository.findAllChatMessages(id, cursorId, pageable);
		SliceResponse<ChatMessageResponse> sliceResponse = new SliceResponse<>(
			slice.getContent(),
			pageable.getPageNumber(),
			pageable.getPageSize(),
			slice.hasNext()
		);

		return ChatRoomResponse.of(id, sliceResponse);
	}
}
