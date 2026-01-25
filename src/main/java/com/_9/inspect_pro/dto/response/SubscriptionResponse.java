package com._9.inspect_pro.dto.response;

import com._9.inspect_pro.model.Subscription;
import com._9.inspect_pro.model.SubscriptionTier;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionResponse(
        Long id,
        Long userId,
        SubscriptionTier tier,
        LocalDate startDate,
        LocalDate endDate,
        boolean isActive,
        LocalDateTime createdAt
) {
    public static SubscriptionResponse from(Subscription subscription) {
        boolean active = subscription.getEndDate() == null || 
                        subscription.getEndDate().isAfter(LocalDate.now());
        
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getTier(),
                subscription.getStartDate(),
                subscription.getEndDate(),
                active,
                subscription.getCreatedAt()
        );
    }
}
