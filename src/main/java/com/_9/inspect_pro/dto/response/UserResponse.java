package com._9.inspect_pro.dto.response;

import java.time.LocalDateTime;

import com._9.inspect_pro.model.User;

public record UserResponse(
        Long id,
        String email,
        LocalDateTime createdAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
    }
}