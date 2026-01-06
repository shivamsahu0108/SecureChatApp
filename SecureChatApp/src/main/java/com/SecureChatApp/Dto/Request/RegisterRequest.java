package com.SecureChatApp.Dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    // crypto fields
    private String publicKey;
    private String encryptedPrivateKey;
    private String mac;
    private String nonce;
    private String salt;
    private String iv;
}
