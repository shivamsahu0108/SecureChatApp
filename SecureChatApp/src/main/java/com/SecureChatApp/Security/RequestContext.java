package com.SecureChatApp.Security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class RequestContext {

    private final String deviceId;
    private final String userAgent;
    private final String ip;

    private RequestContext(String deviceId, String userAgent, String ip) {
        this.deviceId = deviceId;
        this.userAgent = userAgent;
        this.ip = ip;
    }

    public static RequestContext from(HttpServletRequest request) {
        String deviceId = request.getHeader("x-device-id");
        String userAgent = request.getHeader("User-Agent");
        String ip = extractClientIp(request);

        return new RequestContext(deviceId, userAgent, ip);
    }

    private static String extractClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
