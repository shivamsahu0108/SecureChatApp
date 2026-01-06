package com.SecureChatApp.Repository;

import com.SecureChatApp.Model.RefreshSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, Long> {

    Optional<RefreshSession> findByTokenId(String tokenId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RefreshSession r WHERE r.tokenId = :tokenId")
    Optional<RefreshSession> findByTokenIdForUpdate(@Param("tokenId") String tokenId);

    @Modifying
    @Query("UPDATE RefreshSession r SET r.revokedAt = :now WHERE r.userId = :userId AND r.revokedAt IS NULL")
    void revokeAllForUser(@Param("userId") Long userId, @Param("now") Instant now);
}
