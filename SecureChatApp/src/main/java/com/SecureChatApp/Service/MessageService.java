package com.SecureChatApp.Service;

import com.SecureChatApp.Model.Message;

import java.util.List;

public interface MessageService {

    Message sendMessage(Long senderId, Long receiverId, String encryptedMessage);

    List<Message> getConversation(Long userId, Long otherUserId, int limit);

    Message getById(Long id);
}
