package com.SecureChatApp.Dto.Response;

import lombok.Builder;
import lombok.Getter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Builder
public class AuthResponse {
    private boolean ok;
    private String token;
    private String refreshToken;

    // 🔐 ONLY HERE
    @JsonProperty("encrypted_private_key")
    private String encryptedPrivateKey;
    private String mac;
    private String nonce;
    private String iv;
    private String salt;
}
