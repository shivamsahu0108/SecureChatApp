package com.SecureChatApp.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "push_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_push_tokens_user_hash",
                        columnNames = {"user_id", "token_hash"}
                )
        },
        indexes = {
                @Index(name = "idx_push_tokens_user", columnList = "user_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ---------------- USER ---------------- */

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /* ---------------- SECURITY ---------------- */

    // SHA-256 hash of the plaintext push token (for deduplication)
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    // Encrypted token (AES-GCM ciphertext)
    @Lob
    @Column(name = "token_ciphertext", nullable = false)
    private byte[] tokenCiphertext;

    // AES-GCM nonce (12 bytes)
    @Column(name = "nonce", nullable = false, length = 12)
    private byte[] nonce;

    // AES-GCM auth tag (16 bytes)
    @Column(name = "tag", nullable = false, length = 16)
    private byte[] tag;

    /* ---------------- META ---------------- */

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
