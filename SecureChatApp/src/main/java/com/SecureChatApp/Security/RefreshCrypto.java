package com.SecureChatApp.Security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

public final class RefreshCrypto {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String randomBase64Url(int bytes) {
        byte[] buf = new byte[bytes];
        RANDOM.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    public static String sha256Hex(String value) {
        if (value == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(value.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Instant computeExpiry(Duration d) {
        Duration bounded =
                d.compareTo(Duration.ofMinutes(5)) < 0 ? Duration.ofMinutes(5) :
                        d.compareTo(Duration.ofDays(365)) > 0 ? Duration.ofDays(365) : d;
        return Instant.now().plus(bounded);
    }
}
