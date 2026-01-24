package com._9.inspect_pro.dto.request;

import com._9.inspect_pro.model.CredentialType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateCredentialRequest(
        
        @NotNull(message = "Profile ID is required")
        Long profileId,
        
        @NotNull(message = "Credential type is required")
        CredentialType type,
        
        @NotBlank(message = "Issuer is required")
        @Size(max = 100, message = "Issuer cannot exceed 100 characters")
        String issuer,
        
        @NotBlank(message = "License number is required")
        @Size(max = 50, message = "License number cannot exceed 50 characters")
        String licenseNumber,
        
        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate
        
) {}
