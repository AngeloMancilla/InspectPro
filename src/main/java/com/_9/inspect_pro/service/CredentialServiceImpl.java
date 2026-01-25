package com._9.inspect_pro.service;

import com._9.inspect_pro.model.Credential;
import com._9.inspect_pro.model.CredentialStatus;
import com._9.inspect_pro.model.CredentialType;
import com._9.inspect_pro.model.Profile;
import com._9.inspect_pro.repository.CredentialRepository;
import com._9.inspect_pro.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CredentialServiceImpl implements CredentialService {

    private final CredentialRepository credentialRepository;
    private final ProfileRepository profileRepository;

    public CredentialServiceImpl(CredentialRepository credentialRepository, ProfileRepository profileRepository) {
        this.credentialRepository = credentialRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public Credential createCredential(Long profileId, CredentialType type, String issuer, String licenseNumber,
            LocalDate expiryDate) {
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
            
        Credential credential = new Credential();
        credential.setProfile(profile);
        credential.setType(type);
        credential.setStatus(CredentialStatus.PENDING);
        credential.setIssuer(issuer);
        credential.setLicenseNumber(licenseNumber);
        credential.setExpiryDate(expiryDate);
        return credentialRepository.save(credential);
    }

    @Override
    @Transactional
    public Credential approveCredential(Long credentialId) {
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credencial no encontrada"));

        if (credential.getStatus() != CredentialStatus.PENDING) {
            throw new IllegalStateException("Only PENDING credentials can be approved");
        }

        credential.setStatus(CredentialStatus.APPROVED);
        return credentialRepository.save(credential);
    }

    @Override
    @Transactional
    public Credential rejectCredential(Long credentialId) {
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credencial no encontrada"));

        if (credential.getStatus() != CredentialStatus.PENDING) {
            throw new IllegalStateException("Only PENDING credentials can be rejected");
        }

        credential.setStatus(CredentialStatus.REJECTED);
        return credentialRepository.save(credential);
    }

    @Override
    @Transactional
    public Credential markAsExpired(Long credentialId) {
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credencial no encontrada"));

        credential.setStatus(CredentialStatus.EXPIRED);
        return credentialRepository.save(credential);
    }

    @Override
    public List<Credential> findByProfileId(Long profileId) {
        return credentialRepository.findByProfileId(profileId);
    }

    @Override
    public List<Credential> findActiveCredentials(Long profileId) {
        return credentialRepository.findByProfileIdAndStatus(profileId, CredentialStatus.APPROVED);
    }

    @Override
    public List<Credential> findExpiringCredentials(int daysUntilExpiry) {
        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysUntilExpiry);
        return credentialRepository.findExpiringCredentials(today, thresholdDate);
    }

    @Override
    public List<Credential> findAll() {
        return credentialRepository.findAll();
    }

    @Override
    @Transactional
    public Credential updateCredential(Long id, String issuer, String licenseNumber, LocalDate expiryDate) {
        Credential credential = credentialRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Credential not found"));
        credential.setIssuer(issuer);
        credential.setLicenseNumber(licenseNumber);
        credential.setExpiryDate(expiryDate);
        return credentialRepository.save(credential);
    }

    @Override
    public Optional<Credential> findById(Long id) {
        return credentialRepository.findById(id);
    }

    @Override
    @Transactional
    public void deleteCredential(Long credentialId) {
        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new IllegalArgumentException("Credencial no encontrada"));

        if (credential.getStatus() == CredentialStatus.APPROVED) {
            throw new IllegalStateException("Cannot delete approved credentials");
        }

        credentialRepository.deleteById(credentialId);
    }
}
