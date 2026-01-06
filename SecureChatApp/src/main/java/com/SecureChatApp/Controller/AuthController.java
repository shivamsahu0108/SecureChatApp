package com.SecureChatApp.Controller;

import com.SecureChatApp.Dto.Request.LoginRequest;
import com.SecureChatApp.Dto.Request.RegisterRequest;
import com.SecureChatApp.Dto.Response.ApiResponse;
import com.SecureChatApp.Dto.Response.AuthResponse;
import com.SecureChatApp.Dto.Response.UserResponse;
import com.SecureChatApp.Exception.UnauthorizedException;
import com.SecureChatApp.Security.RequestContext;
import com.SecureChatApp.Security.UserPrincipal;
import com.SecureChatApp.Service.AuthService;
import com.SecureChatApp.Service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    /* --------------------------------------------------
     * REGISTER
     * -------------------------------------------------- */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "registration successful",
                        null
                ));
    }

    /* --------------------------------------------------
     * LOGIN
     * -------------------------------------------------- */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest http
    ) {
        RequestContext ctx = RequestContext.from(http);
        AuthResponse response = authService.login(request, ctx);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "login successful",
                        response
                )
        );
    }

    /* --------------------------------------------------
     * REFRESH TOKEN
     * -------------------------------------------------- */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody Map<String, String> body,
            HttpServletRequest http
    ) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Missing refresh token");
        }

        RequestContext ctx = RequestContext.from(http);
        AuthResponse response = authService.refresh(refreshToken, ctx);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "refresh successful",
                        response
                )
        );
    }

    /* --------------------------------------------------
     * LOGOUT
     * -------------------------------------------------- */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
            @RequestBody Map<String, String> body,
            HttpServletRequest http
    ) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Missing refresh token");
        }

        RequestContext ctx = RequestContext.from(http);
        refreshTokenService.revoke(refreshToken, ctx);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "logged out successfully",
                        null
                )
        );
    }

    /* --------------------------------------------------
     * ME
     * -------------------------------------------------- */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, UserResponse>>> me(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        if (principal == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        UserResponse user = authService.me(principal.getUserId());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "ok",
                        Map.of("user", user)
                )
        );
    }

}
