package com.SecureChatApp.Service.ServiceImpl;

import com.SecureChatApp.Exception.UnauthorizedException;
import com.SecureChatApp.Model.RefreshSession;
import com.SecureChatApp.Repository.RefreshSessionRepository;
import com.SecureChatApp.Security.RefreshCrypto;
import com.SecureChatApp.Service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshSessionRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh.expires:7d}")
    private String refreshExpiry;


    @Override
    public String issue(Long userId, String deviceId, String ua, String ip) {

        String tokenId = UUID.randomUUID().toString();
        String secret = RefreshCrypto.randomBase64Url(32);

        RefreshSession session = RefreshSession.builder()
                .userId(userId)
                .tokenId(tokenId)
                .tokenHash(passwordEncoder.encode(secret))
                .deviceId(deviceId != null ? deviceId.substring(0, Math.min(128, deviceId.length())) : null)
                .userAgentHash(RefreshCrypto.sha256Hex(ua))
                .ipHash(RefreshCrypto.sha256Hex(ip))
                .expiresAt(RefreshCrypto.computeExpiry(parseDuration(refreshExpiry)))
                .build();

        repository.save(session);

        return "rt." + tokenId + "." + secret;
    }


    @Override
    @Transactional
    public Long rotate(String refreshToken, String deviceId, String ua, String ip) {

        ParsedToken p = ParsedToken.parse(refreshToken);

        RefreshSession session = repository
                .findByTokenIdForUpdate(p.tokenId)
                .orElseThrow(() -> new UnauthorizedException("refresh token not found"));

        Instant now = Instant.now();

        if (session.getExpiresAt().isBefore(now)
                || session.getRevokedAt() != null
                || session.getReplacedByTokenId() != null) {

            repository.revokeAllForUser(session.getUserId(), now);
            throw new UnauthorizedException("refresh token reused or expired");
        }

        if (session.getDeviceId() != null && !session.getDeviceId().equals(deviceId)) {
            repository.revokeAllForUser(session.getUserId(), now);
            throw new UnauthorizedException("device mismatch");
        }

        if (!passwordEncoder.matches(p.secret, session.getTokenHash())) {
            repository.revokeAllForUser(session.getUserId(), now);
            throw new UnauthorizedException("refresh token invalid");
        }

        // rotate
        String newTokenId = UUID.randomUUID().toString();
        String newSecret = RefreshCrypto.randomBase64Url(32);

        session.setRevokedAt(now);
        session.setReplacedByTokenId(newTokenId);

        RefreshSession next = RefreshSession.builder()
                .userId(session.getUserId())
                .tokenId(newTokenId)
                .tokenHash(passwordEncoder.encode(newSecret))
                .deviceId(session.getDeviceId())
                .userAgentHash(RefreshCrypto.sha256Hex(ua))
                .ipHash(RefreshCrypto.sha256Hex(ip))
                .expiresAt(RefreshCrypto.computeExpiry(parseDuration(refreshExpiry)))
                .build();

        repository.save(next);

        return session.getUserId();
    }


    @Override
    @Transactional
    public void revoke(String refreshToken, String deviceId, String ua, String ip) {

        ParsedToken p = ParsedToken.parse(refreshToken);

        repository.findByTokenId(p.tokenId)
                .ifPresent(session -> {
                    // Optional: device binding check (recommended)
                    if (session.getDeviceId() != null
                            && deviceId != null
                            && !session.getDeviceId().equals(deviceId)) {
                        throw new UnauthorizedException("Device mismatch");
                    }

                    session.setRevokedAt(Instant.now());
                    repository.save(session); // ensure persistence
                });
    }


    @Override
    public void revokeAllForUser(Long userId) {
        repository.revokeAllForUser(userId, Instant.now());
    }


    private Duration parseDuration(String v) {
        if (v.endsWith("d")) return Duration.ofDays(Long.parseLong(v.replace("d", "")));
        if (v.endsWith("h")) return Duration.ofHours(Long.parseLong(v.replace("h", "")));
        if (v.endsWith("m")) return Duration.ofMinutes(Long.parseLong(v.replace("m", "")));
        return Duration.ofDays(7);
    }

    private record ParsedToken(String tokenId, String secret) {
        static ParsedToken parse(String token) {
            if (token == null || !token.startsWith("rt.")) {
                throw new UnauthorizedException("invalid refresh token");
            }
            String[] p = token.split("\\.");
            if (p.length != 3) throw new UnauthorizedException("invalid refresh token");
            return new ParsedToken(p[1], p[2]);
        }
    }
}
