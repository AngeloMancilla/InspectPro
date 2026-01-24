package com._9.inspect_pro.service;

import com._9.inspect_pro.model.CredentialStatus;
import com._9.inspect_pro.model.Profile;
import com._9.inspect_pro.model.ProfileType;
import com._9.inspect_pro.repository.CredentialRepository;
import com._9.inspect_pro.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final CredentialRepository credentialRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository,
                              CredentialRepository credentialRepository) {
        this.profileRepository = profileRepository;
        this.credentialRepository = credentialRepository;
    }

    @Override
    @Transactional
    public Profile createProfile(Long userId, String displayName, ProfileType type) {
        Profile profile = new Profile();
        profile.setDisplayName(displayName);
        profile.setType(type);
        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public Profile upgradeToVerifiedProfessional(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));

        if (!isEligibleForVerifiedProfessional(profileId)) {
            throw new IllegalStateException("Profile does not meet VP requirements (needs ≥2 approved credentials)");
        }

        profile.setType(ProfileType.VERIFIED_PROFESSIONAL);
        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public Profile downgradeToBasic(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));

        profile.setType(ProfileType.BASIC);
        return profileRepository.save(profile);
    }

    @Override
    public boolean isEligibleForVerifiedProfessional(Long profileId) {
        long activeCredentials = credentialRepository.countByProfileIdAndStatus(
            profileId, 
            CredentialStatus.APPROVED
        );
        return activeCredentials >= 2;
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return profileRepository.findById(id);
    }

    @Override
    public List<Profile> findByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Profile updateProfile(Long id, String displayName) {
        Profile profile = profileRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        
        profile.setDisplayName(displayName);
        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new IllegalArgumentException("Profile not found");
        }
        profileRepository.deleteById(profileId);
    }
}
