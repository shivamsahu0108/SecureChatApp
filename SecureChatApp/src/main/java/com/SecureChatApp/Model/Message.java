package com.SecureChatApp.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "idx_messages_sender_receiver_time",
                        columnList = "sender_id, receiver_id, timestamp"),
                @Index(name = "idx_messages_receiver_sender_time",
                        columnList = "receiver_id, sender_id, timestamp")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonProperty("sender_id")
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @com.fasterxml.jackson.annotation.JsonProperty("receiver_id")
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Lob
    @com.fasterxml.jackson.annotation.JsonProperty("encrypted_message")
    @Column(name = "encrypted_message", nullable = false)
    private String encryptedMessage;

    @Column(nullable = false)
    private Instant timestamp;

    @PrePersist
    void onCreate() {
        this.timestamp = Instant.now();
    }
}
