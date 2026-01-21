package com._9.inspect_pro.service;

import com._9.inspect_pro.model.Credential;
import com._9.inspect_pro.model.CredentialStatus;
import com._9.inspect_pro.model.CredentialType;
import com._9.inspect_pro.repository.CredentialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CredentialServiceImpl implements CredentialService {

    private final CredentialRepository credentialRepository;

    public CredentialServiceImpl(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    @Override
    @Transactional
    public Credential createCredential(Long profileId, CredentialType type, String issuer, String licenseNumber,
            LocalDate expiryDate) {
        Credential credential = new Credential();
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
            throw new IllegalStateException("Solo se pueden aprobar credenciales PENDING");
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
            throw new IllegalStateException("Solo se pueden rechazar credenciales PENDING");
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
                .orElseThrow(() -> new IllegalArgumentException("Credencial no encontrada"));

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
            throw new IllegalStateException("No se pueden eliminar credenciales aprobadas");
        }

        credentialRepository.deleteById(credentialId);
    }
}
