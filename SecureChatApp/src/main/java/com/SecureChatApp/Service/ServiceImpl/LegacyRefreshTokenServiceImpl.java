package com.SecureChatApp.Service.ServiceImpl;

import com.SecureChatApp.Model.RefreshToken;
import com.SecureChatApp.Repository.RefreshTokenRepository;
import com.SecureChatApp.Service.LegacyRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LegacyRefreshTokenServiceImpl
        implements LegacyRefreshTokenService {

    private final RefreshTokenRepository repository;

    // == insertToken({ userId, tokenHash, jti }) ==
    @Override
    @Transactional
    public void insertToken(Long userId, String tokenHash, String jti) {
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .token(tokenHash)
                .jti(jti)
                .build();

        repository.save(token);
    }

    // == insertToken({ userId, tokenHash }) ==
    @Override
    @Transactional
    public void insertToken(Long userId, String tokenHash) {
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .token(tokenHash)
                .build();

        repository.save(token);
    }

    // == deleteTokenById ==
    @Override
    @Transactional
    public void deleteTokenById(Long id) {
        repository.deleteById(id);
    }

    // == deleteAllForUser ==
    @Override
    @Transactional
    public void deleteAllForUser(Long userId) {
        repository.deleteByUserId(userId);
    }
}
