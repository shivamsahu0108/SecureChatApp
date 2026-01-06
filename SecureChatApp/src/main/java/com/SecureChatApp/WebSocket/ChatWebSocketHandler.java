package com.SecureChatApp.WebSocket;

import com.SecureChatApp.Model.Message;
import com.SecureChatApp.Repository.MessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Mapping: UserId -> WebSocketSession
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.put(userId, session);
            System.out.println("User connected: " + userId + ", Session: " + session.getId());
            
            // Send explicit connected message
            session.sendMessage(new TextMessage("{\"type\":\"status\",\"status\":\"connected\"}"));
        } else {
            System.out.println("Connection attempt without userId");
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.remove(userId);
            System.out.println("User disconnected: " + userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long senderId = (Long) session.getAttributes().get("userId");
        if (senderId == null) return;

        try {
            JsonNode payload = objectMapper.readTree(message.getPayload());
            
            // Check if it's a message type
            if (payload.has("type") && "message".equals(payload.get("type").asText())) {
                JsonNode data = payload.get("payload");
                
                Long receiverId = data.get("receiver_id").asLong();
                String encryptedContent = data.get("encrypted_message").asText();

                // 1. Save to DB
                Message msg = Message.builder()
                        .senderId(senderId)
                        .receiverId(receiverId)
                        .encryptedMessage(encryptedContent)
                        .timestamp(Instant.now())
                        .build();
                
                messageRepository.save(msg);

                // 2. Send to Receiver if online
                WebSocketSession receiverSession = sessions.get(receiverId);
                if (receiverSession != null && receiverSession.isOpen()) {
                    // Construct outgoing JSON matching what frontend expects
                    // ApiService.dart: _webSocket.messages which goes to ChatScreen
                    // Usually we send pure message object or wrapped
                    
                    String outgoingJson = objectMapper.writeValueAsString(Map.of(
                        "type", "message",
                        "payload", Map.of(
                            "id", msg.getId(),
                            "sender_id", senderId,
                            "receiver_id", receiverId,
                            "encrypted_message", encryptedContent,
                            "timestamp", msg.getTimestamp().toString()
                        )
                    ));
                    
                    receiverSession.sendMessage(new TextMessage(outgoingJson));
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
