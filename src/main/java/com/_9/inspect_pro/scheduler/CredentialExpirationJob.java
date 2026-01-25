package com._9.inspect_pro.scheduler;

import com._9.inspect_pro.model.Credential;
import com._9.inspect_pro.model.CredentialStatus;
import com._9.inspect_pro.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CredentialExpirationJob {

    private final CredentialRepository credentialRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void expireCredentials() {
        log.info("Starting credential expiration job...");
        
        LocalDate today = LocalDate.now();
        List<Credential> expiredCredentials = credentialRepository
                .findByStatusAndExpiryDateBefore(CredentialStatus.APPROVED, today);
        
        if (expiredCredentials.isEmpty()) {
            log.info("No credentials to expire");
            return;
        }
        
        expiredCredentials.forEach(credential -> {
            credential.setStatus(CredentialStatus.EXPIRED);
            credentialRepository.save(credential);
        });
        
        log.info("Expired {} credentials", expiredCredentials.size());
    }
}
