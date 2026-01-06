package com.SecureChatApp.Dto.Response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;


@Getter
@Setter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    @JsonProperty("public_key")
    private String publicKey;

    @JsonProperty("encrypted_private_key")
    private String encryptedPrivateKey;
    private String mac;
    private String nonce;
    private String salt;
    private String iv;


}
