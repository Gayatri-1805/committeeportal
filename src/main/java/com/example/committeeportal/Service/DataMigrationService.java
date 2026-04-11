package com.example.committeeportal.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.committeeportal.Entity.Approver;
import com.example.committeeportal.Entity.Committee;
import com.example.committeeportal.Repository.ApproverRepository;
import com.example.committeeportal.Repository.CommitteeRepository;

@Service
public class DataMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(DataMigrationService.class);

    @Autowired
    private CommitteeRepository committeeRepository;

    @Autowired
    private ApproverRepository approverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Migrate plain text passwords to BCrypt hashes on application startup
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void migratePasswordsOnStartup() {
        logger.info("Starting password migration to BCrypt encryption...");
        try {
            migrateCommitteePasswords();
            migrateApproverPasswords();
            logger.info("Password migration completed successfully");
        } catch (Exception e) {
            logger.error("Error during password migration", e);
        }
    }

    /**
     * Migrate committee passwords from plain text to BCrypt
     */
    private void migrateCommitteePasswords() {
        List<Committee> committees = committeeRepository.findAll();
        int encryptedCount = 0;

        for (Committee committee : committees) {
            if (committee.getPassword() != null && !isBCryptHash(committee.getPassword())) {
                logger.debug("Encrypting password for committee: {}", committee.getContactEmail());
                String encryptedPassword = passwordEncoder.encode(committee.getPassword());
                committee.setPassword(encryptedPassword);
                committeeRepository.save(committee);
                encryptedCount++;
            }
        }

        if (encryptedCount > 0) {
            logger.info("Successfully encrypted {} committee passwords", encryptedCount);
        }
    }

    /**
     * Migrate approver passwords from plain text to BCrypt
     */
    private void migrateApproverPasswords() {
        List<Approver> approvers = approverRepository.findAll();
        int encryptedCount = 0;

        for (Approver approver : approvers) {
            if (approver.getPassword() != null && !isBCryptHash(approver.getPassword())) {
                logger.debug("Encrypting password for approver: {}", approver.getEmail());
                String encryptedPassword = passwordEncoder.encode(approver.getPassword());
                approver.setPassword(encryptedPassword);
                approverRepository.save(approver);
                encryptedCount++;
            }
        }

        if (encryptedCount > 0) {
            logger.info("Successfully encrypted {} approver passwords", encryptedCount);
        }
    }

    /**
     * Check if a password is already BCrypt hashed
     * BCrypt hashes start with $2a$, $2b$, or $2y$
     */
    private boolean isBCryptHash(String password) {
        return password != null && password.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }
}
