package mju.iphak.maru_egg.chat.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import mju.iphak.maru_egg.chat.domain.ChatMessage;
import mju.iphak.maru_egg.chat.domain.QChatMessage;
import mju.iphak.maru_egg.chat.dto.response.ChatMessageResponse;

@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Slice<ChatMessageResponse> findAllChatMessages(final Long chatId, final Long cursorId,
		final Pageable pageable) {
		QChatMessage chatMessage = QChatMessage.chatMessage;
		int pageSize = pageable.getPageSize();

		List<ChatMessage> chatMessages = jpaQueryFactory.selectFrom(chatMessage)
			.where(
				chatMessage.id.eq(chatId),
				cursorId != null ? chatMessage.id.lt(cursorId) : null
			)
			.orderBy(chatMessage.createdAt.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = chatMessages.size() > pageSize;

		if (hasNext) {
			chatMessages.remove(pageSize);
		}

		List<ChatMessageResponse> chatMessageResponses = chatMessages.stream()
			.map(message -> ChatMessageResponse.from(
				message.getContent()
			))
			.collect(Collectors.toList());

		return new SliceImpl<>(chatMessageResponses, pageable, hasNext);
	}
}
