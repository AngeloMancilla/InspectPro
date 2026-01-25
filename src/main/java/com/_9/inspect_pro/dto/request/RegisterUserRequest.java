package com._9.inspect_pro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

public record RegisterUserRequest(

                @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

                @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") String password,

                @NotBlank(message = "Display name is required") @Size(max = 100, message = "Display name cannot exceed 100 characters") String displayName) {
}
