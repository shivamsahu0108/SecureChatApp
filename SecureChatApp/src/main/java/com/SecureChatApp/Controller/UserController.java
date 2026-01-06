package com.SecureChatApp.Controller;

import com.SecureChatApp.Dto.Response.ApiResponse;
import com.SecureChatApp.Dto.Response.PublicUserResponse;
import com.SecureChatApp.Exception.UnauthorizedException;
import com.SecureChatApp.Security.UserPrincipal;
import com.SecureChatApp.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /* --------------------------------------------------
     * GET /api/user/all
     * -------------------------------------------------- */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> listAll(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        List<PublicUserResponse> users =
                userService.listAllExcept(principal.getUserId())
                        .stream()
                        .map(u -> PublicUserResponse.builder()
                                .id(u.getId())
                                .email(u.getEmail())
                                .publicKey(u.getPublicKey())
                                .build()
                        )
                        .toList();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "ok",
                        Map.of("users", users)
                )
        );
    }

    /* --------------------------------------------------
     * GET /api/user/{id}/public-key
     * -------------------------------------------------- */
    @GetMapping("/{id}/public-key")
    public ResponseEntity<ApiResponse<Map<String, Object>>> publicKey(
            @PathVariable Long id
    ) {
        String publicKey = userService.getPublicKey(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "ok",
                        Map.of("publicKey", publicKey)
                )
        );
    }

    /* --------------------------------------------------
     * GET /api/user/contacts
     * -------------------------------------------------- */
    @GetMapping("/contacts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> contacts(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        List<PublicUserResponse> contacts =
                userService.listContacts(principal.getUserId())
                        .stream()
                        .map(u -> PublicUserResponse.builder()
                                .id(u.getId())
                                .email(u.getEmail())
                                .publicKey(u.getPublicKey())
                                .build()
                        )
                        .toList();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "ok",
                        Map.of("contacts", contacts)
                )
        );
    }
}
