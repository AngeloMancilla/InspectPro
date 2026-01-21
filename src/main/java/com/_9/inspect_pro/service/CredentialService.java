package com._9.inspect_pro.service;

import com._9.inspect_pro.model.Credential;
import com._9.inspect_pro.model.CredentialType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CredentialService {

    Credential createCredential(Long profileId, CredentialType type, String issuer, String licenseNumber, LocalDate expiryDate);

    Optional<Credential> findById(Long id);

    List<Credential> findByProfileId(Long profileId);

    Credential approveCredential(Long id);

    Credential rejectCredential(Long id);

    Credential markAsExpired(Long id);

    List<Credential> findActiveCredentials(Long profileId);

    List<Credential> findExpiringCredentials(int daysUntilExpiry);

    void deleteCredential(Long id);

    Credential updateCredential(Long id, String issuer, String licenseNumber, LocalDate expiryDate);

    List<Credential> findAll();
}
