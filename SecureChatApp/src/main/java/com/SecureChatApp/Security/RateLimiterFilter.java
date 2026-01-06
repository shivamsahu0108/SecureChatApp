package com.SecureChatApp.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private static final long WINDOW_MS = 60_000; // 1 minute
    private static final int MAX_REQUESTS = 100;  // adjust as needed

    private final Map<String, Counter> hits = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String key = resolveKey(request);
        long now = System.currentTimeMillis();

        Counter counter = hits.compute(key, (k, v) -> {
            if (v == null || now > v.resetAt) {
                return new Counter(1, now + WINDOW_MS);
            }
            v.count++;
            return v;
        });

        if (counter.count > MAX_REQUESTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "ok": false,
                  "message": "too many requests",
                  "data": {}
                }
            """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveKey(HttpServletRequest req) {
        // Equivalent to req.ip
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    private static class Counter {
        int count;
        long resetAt;

        Counter(int count, long resetAt) {
            this.count = count;
            this.resetAt = resetAt;
        }
    }
}
