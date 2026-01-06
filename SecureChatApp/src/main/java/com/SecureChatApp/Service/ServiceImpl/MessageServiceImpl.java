package com.SecureChatApp.Service.ServiceImpl;

import com.SecureChatApp.Exception.UnauthorizedException;
import com.SecureChatApp.Model.Message;
import com.SecureChatApp.Repository.MessageRepository;
import com.SecureChatApp.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;

    // == insertMessage ==
    @Override
    public Message sendMessage(
            Long senderId,
            Long receiverId,
            String encryptedMessage
    ) {
        if (senderId == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .encryptedMessage(encryptedMessage)
                .build();

        return repository.save(message);
    }

    // == listBetweenUsers ==
    @Override
    public List<Message> getConversation(
            Long userId,
            Long otherUserId,
            int limit
    ) {
        if (userId == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        if (limit <= 0 || limit > 100) {
            limit = 50;
        }

        return repository.findConversation(
                userId,
                otherUserId,
                PageRequest.of(0, limit)
        );
    }

    // == findMessageById ==
    @Override
    public Message getById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
