package com._9.inspect_pro.controller;

import com._9.inspect_pro.dto.request.CreateSubscriptionRequest;
import com._9.inspect_pro.dto.response.SubscriptionResponse;
import com._9.inspect_pro.exception.ResourceNotFoundException;
import com._9.inspect_pro.model.Subscription;
import com._9.inspect_pro.model.SubscriptionTier;
import com._9.inspect_pro.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request) {
        Subscription subscription = subscriptionService.createSubscription(
                request.userId(),
                request.tier(),
                request.startDate(),
                request.endDate()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(SubscriptionResponse.from(subscription));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionById(@PathVariable Long id) {
        Subscription subscription = subscriptionService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
        
        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }

    @GetMapping("/current")
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription(@RequestParam Long userId) {
        Subscription subscription = subscriptionService.findActiveByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user: " + userId));
        
        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptionsByUserId(@PathVariable Long userId) {
        List<SubscriptionResponse> subscriptions = subscriptionService.findByUserId(userId)
                .stream()
                .map(SubscriptionResponse::from)
                .toList();
        
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping("/check-feature")
    public ResponseEntity<Map<String, Object>> checkFeatureAccess(
            @RequestParam Long userId,
            @RequestParam String feature) {
        
        int publicationLimit = subscriptionService.getPublicationLimit(userId);
        boolean hasPremiumAccess = subscriptionService.hasPremiumAccess(userId);
        
        Map<String, Object> response = Map.of(
                "feature", feature,
                "hasAccess", hasPremiumAccess,
                "publicationLimit", publicationLimit
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/upgrade")
    public ResponseEntity<SubscriptionResponse> upgradeTier(
            @PathVariable Long id,
            @RequestParam SubscriptionTier tier) {
        
        Subscription subscription = subscriptionService.upgradeTier(id, tier);
        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }

    @PostMapping("/{id}/downgrade")
    public ResponseEntity<SubscriptionResponse> downgradeTier(
            @PathVariable Long id,
            @RequestParam SubscriptionTier tier) {
        
        Subscription subscription = subscriptionService.downgradeTier(id, tier);
        return ResponseEntity.ok(SubscriptionResponse.from(subscription));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long id) {
        subscriptionService.cancelSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/webhooks/stripe")
    public ResponseEntity<Map<String, String>> handleStripeWebhook(@RequestBody Map<String, Object> payload) {
        String eventType = (String) payload.get("type");
        
        switch (eventType) {
            case "customer.subscription.created":
            case "customer.subscription.updated":
            case "customer.subscription.deleted":
                break;
            default:
                break;
        }
        
        return ResponseEntity.ok(Map.of("received", "true"));
    }
}
