package com.SecureChatApp.Controller;

import com.SecureChatApp.Dto.ChatMessage;
import com.SecureChatApp.Model.Message;
import com.SecureChatApp.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepository;

    @MessageMapping("/chat.send")
    @SendToUser("/queue/messages")
    public ChatMessage send(ChatMessage payload, Principal principal) {

        Long senderId = Long.parseLong(principal.getName());

        var msg = messageRepository.save(
                new Message(
                        null,
                        senderId,
                        payload.getReceiverId(),
                        payload.getEncryptedMessage(),
                        null
                )
        );

        return new ChatMessage(
                msg.getId(),
                senderId,
                payload.getReceiverId(),
                msg.getEncryptedMessage(),
                msg.getTimestamp()
        );
    }
}

