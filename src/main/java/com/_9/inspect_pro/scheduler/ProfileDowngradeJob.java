package com._9.inspect_pro.scheduler;

import com._9.inspect_pro.model.CredentialStatus;
import com._9.inspect_pro.model.Profile;
import com._9.inspect_pro.model.ProfileType;
import com._9.inspect_pro.repository.CredentialRepository;
import com._9.inspect_pro.repository.ProfileRepository;
import com._9.inspect_pro.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileDowngradeJob {

    private final ProfileRepository profileRepository;
    private final CredentialRepository credentialRepository;
    private final ProfileService profileService;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void downgradeProfilesWithoutCredentials() {
        log.info("Starting profile downgrade job...");
        
        List<Profile> verifiedProfiles = profileRepository
                .findByType(ProfileType.VERIFIED_PROFESSIONAL);
        
        int downgradeCount = 0;
        
        for (Profile profile : verifiedProfiles) {
            Long activeCredentials = credentialRepository
                    .countByProfileIdAndStatus(profile.getId(), CredentialStatus.APPROVED);
            
            Long pendingCredentials = credentialRepository
                    .countByProfileIdAndStatus(profile.getId(), CredentialStatus.PENDING);
            
            if (activeCredentials == 0 && pendingCredentials == 0) {
                profileService.downgradeToBasic(profile.getId());
                downgradeCount++;
                log.debug("Downgraded profile {} (no active or pending credentials)", profile.getId());
            } else if (activeCredentials == 0 && pendingCredentials > 0) {
                log.debug("Profile {} kept VP status (grace period: {} pending credentials)", 
                        profile.getId(), pendingCredentials);
            }
        }
        
        if (downgradeCount == 0) {
            log.info("No profiles to downgrade");
        } else {
            log.info("Downgraded {} profiles to BASIC", downgradeCount);
        }
    }
}
