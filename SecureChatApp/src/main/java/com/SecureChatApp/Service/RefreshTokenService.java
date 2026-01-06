package com.SecureChatApp.Service;

import com.SecureChatApp.Security.RequestContext;

public interface RefreshTokenService {

    String issue(Long userId, String deviceId, String userAgent, String ip);

    Long rotate(String refreshToken, String deviceId, String userAgent, String ip);

    void revoke(String refreshToken, String deviceId, String userAgent, String ip);

    void revokeAllForUser(Long userId);

    /* ---------- convenience overload ---------- */
    default void revoke(String refreshToken, RequestContext ctx) {
        revoke(
                refreshToken,
                ctx.getDeviceId(),
                ctx.getUserAgent(),
                ctx.getIp()
        );
    }
}
