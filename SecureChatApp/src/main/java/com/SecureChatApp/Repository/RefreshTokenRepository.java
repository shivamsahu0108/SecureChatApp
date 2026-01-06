package com.SecureChatApp.Repository;

import com.SecureChatApp.Model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    List<RefreshToken> findByUserId(Long userId);

    Optional<RefreshToken> findByUserIdAndJti(Long userId, String jti);

    void deleteByUserId(Long userId);
}
