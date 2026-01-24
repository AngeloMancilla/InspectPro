package com._9.inspect_pro.dto.request;

import com._9.inspect_pro.model.ProfileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProfileRequest(
        
        @NotNull(message = "User ID is required")
        Long userId,
        
        @NotBlank(message = "Display name is required")
        @Size(max = 100, message = "Display name cannot exceed 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9 -]+$", message = "Display name can only contain alphanumeric characters, spaces, and hyphens")
        String displayName,
        
        @NotNull(message = "Profile type is required")
        ProfileType type
        
) {}
