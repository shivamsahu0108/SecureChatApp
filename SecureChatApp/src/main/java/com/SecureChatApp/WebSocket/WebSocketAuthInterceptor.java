package com.SecureChatApp.WebSocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.List;
import com.SecureChatApp.Security.JwtUtil;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        List<String> protocols = request.getHeaders().get("Sec-WebSocket-Protocol");
        String token = null;

        if (protocols != null && !protocols.isEmpty()) {
            boolean hasBearer = false;
            for (String protocol : protocols) {
                // Client usually sends "bearer, <token>" string or ["bearer", "<token>"]
                String[] parts = protocol.split(",");
                for (String part : parts) {
                    String trimmed = part.trim();
                    if ("bearer".equalsIgnoreCase(trimmed)) {
                        hasBearer = true;
                    } else {
                        // Assume any non-bearer string is the token
                        if (!trimmed.isEmpty()) {
                            token = trimmed;
                        }
                    }
                }
            }
            
            if (hasBearer) {
                response.getHeaders().set("Sec-WebSocket-Protocol", "bearer");
            }
        }

        if (token != null && jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.extractUserId(token);
            attributes.put("userId", userId);
            return true;
        }

        System.out.println("WebSocket handshake failed: No valid token found");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
