package com._9.inspect_pro.repository;

import com._9.inspect_pro.model.Subscription;
import com._9.inspect_pro.model.SubscriptionTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long userId);

    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.endDate >= :currentDate ORDER BY s.endDate DESC")
    Optional<Subscription> findActiveSubscription(
            @Param("userId") Long userId,
            @Param("currentDate") LocalDate currentDate);

    List<Subscription> findByTier(SubscriptionTier tier);

    @Query("SELECT s FROM Subscription s WHERE s.endDate BETWEEN :startDate AND :endDate")
    List<Subscription> findExpiringSubscriptions(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}