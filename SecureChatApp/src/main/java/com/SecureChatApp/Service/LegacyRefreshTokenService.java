package com.SecureChatApp.Service;

public interface LegacyRefreshTokenService {

    void insertToken(Long userId, String tokenHash, String jti);

    void insertToken(Long userId, String tokenHash);

    void deleteTokenById(Long id);

    void deleteAllForUser(Long userId);
}
