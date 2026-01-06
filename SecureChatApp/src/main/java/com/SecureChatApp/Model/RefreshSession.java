package com.SecureChatApp.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_sessions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_refresh_sessions_token_id",
                        columnNames = "token_id"
                )
        },
        indexes = {
                @Index(name = "idx_refresh_sessions_user_id", columnList = "user_id"),
                @Index(name = "idx_refresh_sessions_expires_at", columnList = "expires_at"),
                @Index(name = "idx_refresh_sessions_revoked_at", columnList = "revoked_at"),
                @Index(name = "idx_refresh_sessions_user_revoked", columnList = "user_id, revoked_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_id", nullable = false, length = 36)
    private String tokenId;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "device_id", length = 128)
    private String deviceId;

    @Column(name = "user_agent_hash", length = 64)
    private String userAgentHash;

    @Column(name = "ip_hash", length = 64)
    private String ipHash;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by_token_id", length = 36)
    private String replacedByTokenId;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
