package com.SecureChatApp.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class CryptoConfig {

    @Value("${security.push-token.aes-key-base64}")
    private String base64Key;

    @Bean
    public SecretKey pushTokenSecretKey() {
        byte[] decoded = Base64.getDecoder().decode(base64Key);

        if (decoded.length != 32) {
            throw new IllegalStateException("Push token AES key must be 256-bit (32 bytes)");
        }

        return new SecretKeySpec(decoded, "AES");
    }
}
