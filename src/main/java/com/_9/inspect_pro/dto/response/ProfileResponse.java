package com._9.inspect_pro.dto.response;

import com._9.inspect_pro.model.Profile;
import com._9.inspect_pro.model.ProfileType;

import java.time.LocalDateTime;

public record ProfileResponse(
        Long id,
        Long userId,
        String displayName,
        ProfileType type,
        LocalDateTime createdAt
) {
    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getDisplayName(),
                profile.getType(),
                profile.getCreatedAt()
        );
    }
}
