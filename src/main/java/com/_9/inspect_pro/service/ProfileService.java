package com._9.inspect_pro.service;

import com._9.inspect_pro.model.Profile;
import com._9.inspect_pro.model.ProfileType;

import java.util.List;
import java.util.Optional;

public interface ProfileService {

    Profile createProfile(Long userId, String displayName, ProfileType type);

    Optional<Profile> findById(Long id);

    List<Profile> findByUserId(Long userId);

    Profile updateProfile(Long id, String displayName);

    void deleteProfile(Long id);

    Profile upgradeToVerifiedProfessional(Long profileId);

    Profile downgradeToBasic(Long profileId);

    boolean isEligibleForVerifiedProfessional(Long profileId);
}
