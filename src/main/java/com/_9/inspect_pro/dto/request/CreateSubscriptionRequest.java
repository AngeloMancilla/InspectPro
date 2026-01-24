package com._9.inspect_pro.dto.request;

import com._9.inspect_pro.model.SubscriptionTier;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateSubscriptionRequest(
        
        @NotNull(message = "User ID is required")
        Long userId,
        
        @NotNull(message = "Subscription tier is required")
        SubscriptionTier tier,
        
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        
        @NotNull(message = "End date is required")
        LocalDate endDate
        
) {}
