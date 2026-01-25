package com._9.inspect_pro.service;

import com._9.inspect_pro.model.Subscription;
import com._9.inspect_pro.model.SubscriptionTier;
import com._9.inspect_pro.model.User;
import com._9.inspect_pro.repository.SubscriptionRepository;
import com._9.inspect_pro.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Subscription createSubscription(Long userId, SubscriptionTier tier, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setTier(tier);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription renewSubscription(Long id, LocalDate newEndDate) {
        Subscription subscription = subscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.setEndDate(newEndDate);
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription upgradeTier(Long id, SubscriptionTier newTier) {
        Subscription subscription = subscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if (subscription.getTier().ordinal() >= newTier.ordinal()) {
            throw new IllegalArgumentException("New tier must be higher than current tier");
        }

        subscription.setTier(newTier);
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public Subscription downgradeTier(Long id, SubscriptionTier newTier) {
        Subscription subscription = subscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        if (subscription.getTier().ordinal() <= newTier.ordinal()) {
            throw new IllegalArgumentException("New tier must be lower than current tier");
        }

        subscription.setTier(newTier);
        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void cancelSubscription(Long id) {
        Subscription subscription = subscriptionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        subscription.setEndDate(LocalDate.now());
        subscriptionRepository.save(subscription);
    }

    @Override
    public Optional<Subscription> findActiveByUserId(Long userId) {
        return subscriptionRepository.findActiveSubscription(userId, LocalDate.now());
    }

    @Override
    public int getPublicationLimit(Long userId) {
        Optional<Subscription> activeSubscription = findActiveByUserId(userId);

        if (activeSubscription.isEmpty()) {
            return 5;
        }

        return switch (activeSubscription.get().getTier()) {
            case BASIC -> 5;
            case ENHANCED -> 20;
            case PROFESSIONAL -> Integer.MAX_VALUE;
        };
    }

    @Override
    public boolean hasPremiumAccess(Long userId) {
        return findActiveByUserId(userId)
            .map(sub -> sub.getTier() == SubscriptionTier.PROFESSIONAL)
            .orElse(Boolean.FALSE);
    }

    @Override
    public List<Subscription> findByUserId(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Override
    public Optional<Subscription> findById(Long id) {
        return subscriptionRepository.findById(id);
    }
}
