package com.SecureChatApp.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private Long id;

    private Long senderId;

    private Long receiverId;

    private String encryptedMessage;

    private Instant timestamp;
}
