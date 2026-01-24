package com._9.inspect_pro.dto.response;

import com._9.inspect_pro.model.Credential;
import com._9.inspect_pro.model.CredentialStatus;
import com._9.inspect_pro.model.CredentialType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CredentialResponse(
        Long id,
        Long profileId,
        CredentialType type,
        CredentialStatus status,
        String issuer,
        String licenseNumber,
        LocalDate expiryDate,
        LocalDateTime createdAt
) {
    public static CredentialResponse from(Credential credential) {
        return new CredentialResponse(
                credential.getId(),
                credential.getProfile().getId(),
                credential.getType(),
                credential.getStatus(),
                credential.getIssuer(),
                credential.getLicenseNumber(),
                credential.getExpiryDate(),
                credential.getCreatedAt()
        );
    }
}
