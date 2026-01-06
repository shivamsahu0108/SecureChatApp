package com.SecureChatApp.Service.ServiceImpl;

import com.SecureChatApp.Dto.Request.LoginRequest;
import com.SecureChatApp.Dto.Request.RegisterRequest;
import com.SecureChatApp.Dto.Response.AuthResponse;
import com.SecureChatApp.Dto.Response.UserResponse;
import com.SecureChatApp.Exception.BadRequestException;
import com.SecureChatApp.Exception.ResourceNotFoundException;
import com.SecureChatApp.Exception.UnauthorizedException;
import com.SecureChatApp.Model.User;
import com.SecureChatApp.Repository.UserRepository;
import com.SecureChatApp.Security.JwtUtil;
import com.SecureChatApp.Security.RequestContext;
import com.SecureChatApp.Service.AuthService;
import com.SecureChatApp.Service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /* =====================================================
     * REGISTER
     * ===================================================== */
    @Override
    public void register(RegisterRequest req) {

        if (userRepository.findByEmail(req.getEmail().trim().toLowerCase()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .email(req.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .publicKey(req.getPublicKey())
                .encryptedPrivateKey(req.getEncryptedPrivateKey())
                .mac(req.getMac())
                .nonce(req.getNonce())
                .salt(req.getSalt())
                .iv(req.getIv())
                .build();

        userRepository.save(user);
    }

    /* =====================================================
     * LOGIN
     * ===================================================== */
    @Override
    public AuthResponse login(LoginRequest req, RequestContext ctx) {

        User user = userRepository.findByEmail(req.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Production safety (matches Node behavior)
        if (isProduction() && ctx.getDeviceId() == null) {
            throw new BadRequestException("x-device-id header is required");
        }

        String accessToken =
                jwtUtil.generateToken(user.getId(), user.getEmail());

        String refreshToken =
                refreshTokenService.issue(
                        user.getId(),
                        ctx.getDeviceId(),
                        ctx.getUserAgent(),
                        ctx.getIp()
                );

        // 🔴 IMPORTANT: Send encrypted private key material
        return AuthResponse.builder()
                .ok(true)
                .token(accessToken)
                .refreshToken(refreshToken)

                // Required for Flutter PrivateKeyStore
                .encryptedPrivateKey(user.getEncryptedPrivateKey())
                .mac(user.getMac())
                .nonce(user.getNonce())
                .iv(user.getIv())
                .salt(user.getSalt())
                .build();
    }

    /* =====================================================
     * REFRESH TOKEN
     * ===================================================== */
    @Override
    public AuthResponse refresh(String refreshToken, RequestContext ctx) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token is required");
        }

        if (isProduction() && ctx.getDeviceId() == null) {
            throw new BadRequestException("x-device-id header is required");
        }

        Long userId =
                refreshTokenService.rotate(
                        refreshToken,
                        ctx.getDeviceId(),
                        ctx.getUserAgent(),
                        ctx.getIp()
                );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        String newAccessToken =
                jwtUtil.generateToken(user.getId(), user.getEmail());

        String newRefreshToken =
                refreshTokenService.issue(
                        user.getId(),
                        ctx.getDeviceId(),
                        ctx.getUserAgent(),
                        ctx.getIp()
                );

        return AuthResponse.builder()
                .ok(true)
                .token(newAccessToken)
                .refreshToken(newRefreshToken)

                // 🔴 REQUIRED AGAIN (Flutter refresh flow)
                .encryptedPrivateKey(user.getEncryptedPrivateKey())
                .mac(user.getMac())
                .nonce(user.getNonce())
                .iv(user.getIv())
                .salt(user.getSalt())
                .build();
    }

    /* =====================================================
     * ME
     * ===================================================== */
    @Override
    public UserResponse me(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .publicKey(user.getPublicKey())

                // Needed for client storage restore
                .encryptedPrivateKey(user.getEncryptedPrivateKey())
                .mac(user.getMac())
                .nonce(user.getNonce())
                .salt(user.getSalt())
                .iv(user.getIv())
                .build();
    }

    /* =====================================================
     * ENV CHECK
     * ===================================================== */
    private boolean isProduction() {
        return "prod".equalsIgnoreCase(activeProfile)
                || "production".equalsIgnoreCase(activeProfile);
    }
}
