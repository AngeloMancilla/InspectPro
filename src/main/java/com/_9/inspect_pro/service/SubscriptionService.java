package com._9.inspect_pro.service;

import com._9.inspect_pro.model.Subscription;
import com._9.inspect_pro.model.SubscriptionTier;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionService {

    Subscription createSubscription(Long userId, SubscriptionTier tier, LocalDate startDate, LocalDate endDate);

    Optional<Subscription> findById(Long id);

    Optional<Subscription> findActiveByUserId(Long userId);

    List<Subscription> findByUserId(Long userId);

    Subscription upgradeTier(Long id, SubscriptionTier newTier);

    Subscription downgradeTier(Long id, SubscriptionTier newTier);

    Subscription renewSubscription(Long id, LocalDate newEndDate);

    void cancelSubscription(Long id);

    boolean hasPremiumAccess(Long userId);

    int getPublicationLimit(Long userId);
}
