package com.SecureChatApp.Dto.Response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class PublicUserResponse {

    private Long id;
    private String email;
    private String publicKey;
}
