package com.SecureChatApp.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_users_email", columnNames = "email")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Lob
    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Lob
    @Column(name = "encrypted_private_key", nullable = false)
    private String encryptedPrivateKey;

    @Column(nullable = false)
    private String mac;

    @Column(nullable = false)
    private String nonce;

    @Column(nullable = false)
    private String salt;

    @Column(nullable = false)
    private String iv;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
