package com._9.inspect_pro.controller;

import com._9.inspect_pro.dto.request.CreateCredentialRequest;
import com._9.inspect_pro.dto.response.CredentialResponse;
import com._9.inspect_pro.exception.ResourceNotFoundException;
import com._9.inspect_pro.model.Credential;
import com._9.inspect_pro.service.CredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @PostMapping
    public ResponseEntity<CredentialResponse> createCredential(@Valid @RequestBody CreateCredentialRequest request) {
        Credential credential = credentialService.createCredential(
                request.profileId(),
                request.type(),
                request.issuer(),
                request.licenseNumber(),
                request.expiryDate()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(CredentialResponse.from(credential));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CredentialResponse> getCredentialById(@PathVariable Long id) {
        Credential credential = credentialService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found with id: " + id));
        
        return ResponseEntity.ok(CredentialResponse.from(credential));
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<List<CredentialResponse>> getCredentialsByProfileId(@PathVariable Long profileId) {
        List<CredentialResponse> credentials = credentialService.findByProfileId(profileId)
                .stream()
                .map(CredentialResponse::from)
                .toList();
        
        return ResponseEntity.ok(credentials);
    }

    @GetMapping("/profile/{profileId}/active")
    public ResponseEntity<List<CredentialResponse>> getActiveCredentials(@PathVariable Long profileId) {
        List<CredentialResponse> credentials = credentialService.findActiveCredentials(profileId)
                .stream()
                .map(CredentialResponse::from)
                .toList();
        
        return ResponseEntity.ok(credentials);
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<CredentialResponse>> getExpiringCredentials(
            @RequestParam(defaultValue = "30") int days) {
        
        List<CredentialResponse> credentials = credentialService.findExpiringCredentials(days)
                .stream()
                .map(CredentialResponse::from)
                .toList();
        
        return ResponseEntity.ok(credentials);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<CredentialResponse> approveCredential(@PathVariable Long id) {
        Credential credential = credentialService.approveCredential(id);
        return ResponseEntity.ok(CredentialResponse.from(credential));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<CredentialResponse> rejectCredential(@PathVariable Long id) {
        Credential credential = credentialService.rejectCredential(id);
        return ResponseEntity.ok(CredentialResponse.from(credential));
    }

    @PostMapping("/{id}/expire")
    public ResponseEntity<CredentialResponse> markAsExpired(@PathVariable Long id) {
        Credential credential = credentialService.markAsExpired(id);
        return ResponseEntity.ok(CredentialResponse.from(credential));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredential(@PathVariable Long id) {
        credentialService.deleteCredential(id);
        return ResponseEntity.noContent().build();
    }
}
