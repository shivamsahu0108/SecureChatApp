package com.SecureChatApp.Repository;

import com.SecureChatApp.Model.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByUserIdAndTokenHash(Long userId, String tokenHash);

    void deleteByUserId(Long userId);
}
