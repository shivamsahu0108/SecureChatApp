package com.SecureChatApp.WebSocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public final class WsAuthUtil {

    private WsAuthUtil() {}

    public static String extractToken(WebSocketSession session) {

        List<String> proto =
                session.getHandshakeHeaders().get("Sec-WebSocket-Protocol");

        if (proto != null && !proto.isEmpty()) {
            String[] parts = proto.get(0).split(",");
            if (parts.length == 2 && parts[0].equalsIgnoreCase("bearer")) {
                return parts[1].trim();
            }
        }
        return null;
    }

}
