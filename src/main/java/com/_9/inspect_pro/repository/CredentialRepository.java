package com._9.inspect_pro.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com._9.inspect_pro.model.Credential;
import com._9.inspect_pro.model.CredentialStatus;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    List<Credential> findByProfileId(Long profileId);

    List<Credential> findByStatus(CredentialStatus status);

    List<Credential> findByProfileIdAndStatus(Long profileId, CredentialStatus status);

    @Query("SELECT c FROM Credential c WHERE c.status = 'Active' AND c.expiryDate BETWEEN :startDate AND :endDate")
    List<Credential> findExpiringCredentials(
            @Param("startDate") LocalDate starDate,
            @Param("endDate") LocalDate enDate);

    Long countByProfileIdAndStatus(Long profileId, CredentialStatus status);
    
    List<Credential> findByStatusAndExpiryDateBefore(CredentialStatus status, LocalDate date);
}
