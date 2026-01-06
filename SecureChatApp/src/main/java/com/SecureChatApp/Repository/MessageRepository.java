package com.SecureChatApp.Repository;

import com.SecureChatApp.Model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // == listBetweenUsers ==
    @Query("""
        SELECT m FROM Message m
        WHERE (m.senderId = :user1 AND m.receiverId = :user2)
           OR (m.senderId = :user2 AND m.receiverId = :user1)
        ORDER BY m.timestamp ASC
    """)
    List<Message> findConversation(
            Long user1,
            Long user2,
            Pageable pageable
    );
}
