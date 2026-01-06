package com.SecureChatApp.Service.ServiceImpl;

import com.SecureChatApp.Model.PushToken;
import com.SecureChatApp.Repository.PushTokenRepository;
import com.SecureChatApp.Security.PushTokenCrypto;
import com.SecureChatApp.Service.PushTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class PushTokenServiceImpl implements PushTokenService {

    private final PushTokenRepository repository;
    private final SecretKey pushTokenKey;

    @Override
    public void registerPushToken(Long userId, String rawToken) {

        String tokenHash = PushTokenCrypto.sha256Hex(rawToken);

        // Idempotent insert
        if (repository.findByUserIdAndTokenHash(userId, tokenHash).isPresent()) {
            return;
        }

        var enc = PushTokenCrypto.encrypt(rawToken, pushTokenKey);

        PushToken entity = PushToken.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .tokenCiphertext(enc.ciphertext())
                .nonce(enc.nonce())
                .tag(enc.tag())
                .build();

        repository.save(entity);
    }

    @Override
    public void removeAllForUser(Long userId) {
        repository.deleteByUserId(userId);
    }
}
