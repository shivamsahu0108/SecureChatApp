package com.SecureChatApp.Service;

public interface PushTokenService {

    void registerPushToken(Long userId, String rawPushToken);

    void removeAllForUser(Long userId);
}
