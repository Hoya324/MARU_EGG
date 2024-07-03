package mju.iphak.maru_egg.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mju.iphak.maru_egg.chat.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	Optional<ChatRoom> findById(Long id);
}
