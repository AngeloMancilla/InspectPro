package com._9.inspect_pro.controller;

import com._9.inspect_pro.dto.request.LoginRequest;
import com._9.inspect_pro.dto.request.RegisterUserRequest;
import com._9.inspect_pro.dto.response.TokenResponse;
import com._9.inspect_pro.exception.DuplicateResourceException;
import com._9.inspect_pro.exception.InvalidCredentialsException;
import com._9.inspect_pro.model.User;
import com._9.inspect_pro.security.JwtUtil;
import com._9.inspect_pro.service.RefreshTokenService;
import com._9.inspect_pro.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        if (userService.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Create user with default BASIC profile
        User user = userService.registerUser(
                request.email(),
                request.password(),
                request.displayName()
        );

        Long profileId = user.getProfiles().get(0).getId();

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), profileId);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        refreshTokenService.storeRefreshToken(user.getEmail(), refreshToken);

        TokenResponse response = TokenResponse.of(accessToken, refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.authenticateUser(request.email(), request.password());

        if (user == null) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Use first profile as active (TODO: implement profile switching)
        Long profileId = user.getProfiles().isEmpty() ? null : user.getProfiles().get(0).getId();

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), profileId);
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        
        refreshTokenService.storeRefreshToken(user.getEmail(), refreshToken);

        TokenResponse response = TokenResponse.of(accessToken, refreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.replace("Bearer ", "");

        String email = jwtUtil.extractEmail(token);
        
        if (!jwtUtil.validateToken(token, email) || !refreshTokenService.validateRefreshToken(email, token)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        Long profileId = user.getProfiles().isEmpty() ? null : user.getProfiles().get(0).getId();

        String newAccessToken = jwtUtil.generateToken(email, user.getId(), profileId);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);
        
        refreshTokenService.storeRefreshToken(email, newRefreshToken);

        TokenResponse response = TokenResponse.of(newAccessToken, newRefreshToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmail(authHeader.replace("Bearer ", ""));
        refreshTokenService.revokeRefreshToken(email);
        return ResponseEntity.noContent().build();
    }
}
