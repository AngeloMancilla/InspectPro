package com._9.inspect_pro.controller;

import com._9.inspect_pro.dto.request.CreateProfileRequest;
import com._9.inspect_pro.dto.response.ProfileResponse;
import com._9.inspect_pro.exception.ResourceNotFoundException;
import com._9.inspect_pro.model.Profile;
import com._9.inspect_pro.model.User;
import com._9.inspect_pro.service.ProfileService;
import com._9.inspect_pro.service.ProfileSessionService;
import com._9.inspect_pro.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final ProfileSessionService profileSessionService;

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        Profile profile = profileService.createProfile(
                request.userId(),
                request.displayName(),
                request.type()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ProfileResponse.from(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable Long id) {
        Profile profile = profileService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
        
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProfileResponse>> getProfilesByUserId(@PathVariable Long userId) {
        List<ProfileResponse> profiles = profileService.findByUserId(userId)
                .stream()
                .map(ProfileResponse::from)
                .toList();
        
        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable Long id,
            @RequestParam String displayName) {
        
        Profile profile = profileService.updateProfile(id, displayName);
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PostMapping("/{id}/upgrade")
    public ResponseEntity<ProfileResponse> upgradeToVerifiedProfessional(@PathVariable Long id) {
        Profile profile = profileService.upgradeToVerifiedProfessional(id);
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PostMapping("/{id}/downgrade")
    public ResponseEntity<ProfileResponse> downgradeToBasic(@PathVariable Long id) {
        Profile profile = profileService.downgradeToBasic(id);
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @GetMapping("/{id}/vp-eligible")
    public ResponseEntity<Boolean> isEligibleForVP(@PathVariable Long id) {
        boolean eligible = profileService.isEligibleForVerifiedProfessional(id);
        return ResponseEntity.ok(eligible);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getCurrentProfile() {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Long activeProfileId = profileSessionService.getActiveProfile(user.getId());
        
        Profile profile;
        if (activeProfileId != null) {
            profile = profileService.findById(activeProfileId)
                    .orElse(user.getProfiles().isEmpty() ? null : user.getProfiles().get(0));
        } else {
            profile = user.getProfiles().isEmpty() ? null : user.getProfiles().get(0);
        }
        
        if (profile == null) {
            throw new ResourceNotFoundException("No profile found for current user");
        }
        
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateCurrentProfile(@RequestParam String displayName) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Long activeProfileId = profileSessionService.getActiveProfile(user.getId());
        
        Profile profile;
        if (activeProfileId != null) {
            profile = profileService.findById(activeProfileId)
                    .orElse(user.getProfiles().isEmpty() ? null : user.getProfiles().get(0));
        } else {
            profile = user.getProfiles().isEmpty() ? null : user.getProfiles().get(0);
        }
        
        if (profile == null) {
            throw new ResourceNotFoundException("No profile found for current user");
        }
        
        Profile updated = profileService.updateProfile(profile.getId(), displayName);
        return ResponseEntity.ok(ProfileResponse.from(updated));
    }

    @PostMapping("/switch/{profileId}")
    public ResponseEntity<ProfileResponse> switchProfile(@PathVariable Long profileId) {
        String email = getCurrentUserEmail();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Profile profile = profileService.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));
        
        if (!profile.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Profile does not belong to current user");
        }
        
        profileSessionService.setActiveProfile(user.getId(), profileId);
        
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
