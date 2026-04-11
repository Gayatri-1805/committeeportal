package com.example.committeeportal.Service;

import com.example.committeeportal.DTO.LoginRequest;
import com.example.committeeportal.DTO.LoginResponse;
import com.example.committeeportal.Entity.Approver;
import com.example.committeeportal.Entity.Committee;
import com.example.committeeportal.Repository.ApproverRepository;
import com.example.committeeportal.Repository.CommitteeRepository;
import com.example.committeeportal.Security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private CommitteeRepository committeeRepository;

    @Autowired
    private ApproverRepository approverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Committee login
     */
    public LoginResponse loginCommittee(LoginRequest loginRequest) {
        logger.info("Committee login attempt for email: {}", loginRequest.getEmail());

        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            logger.warn("Login failed: missing email or password");
            throw new IllegalArgumentException("Email and password are required");
        }

        Committee committee = committeeRepository.findByContactEmailIgnoreCase(loginRequest.getEmail());

        if (committee != null && passwordEncoder.matches(loginRequest.getPassword(), committee.getPassword())) {
            logger.info("Login successful for committee email: {}", loginRequest.getEmail());
            String token = jwtUtil.generateToken(committee.getContactEmail(), committee.getId(), "COMMITTEE");
            return new LoginResponse(token, committee.getContactEmail(), committee.getId(), 
                                     committee.getCommitteeName(), "COMMITTEE");
        } else {
            logger.warn("Login failed for email: {} - Invalid credentials", loginRequest.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    /**
     * Approver login
     */
    public LoginResponse loginApprover(LoginRequest loginRequest) {
        logger.info("Approver login attempt for email: {}", loginRequest.getEmail());

        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            logger.warn("Login failed: missing email or password");
            throw new IllegalArgumentException("Email and password are required");
        }

        Approver approver = approverRepository.findByEmailIgnoreCase(loginRequest.getEmail());

        if (approver != null && passwordEncoder.matches(loginRequest.getPassword(), approver.getPassword())) {
            logger.info("Login successful for approver email: {}", loginRequest.getEmail());
            String token = jwtUtil.generateToken(approver.getEmail(), approver.getApproverId(), approver.getRole());
            return new LoginResponse(token, approver.getEmail(), approver.getApproverId(), 
                                     approver.getName(), approver.getRole());
        } else {
            logger.warn("Login failed for email: {} - Invalid credentials", loginRequest.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    /**
     * Register new committee with encrypted password
     */
    public Committee registerCommittee(Committee committee) {
        logger.info("Registering new committee: {}", committee.getCommitteeName());
        
        if (committee.getPassword() == null || committee.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Encrypt password before saving
        committee.setPassword(passwordEncoder.encode(committee.getPassword()));
        Committee saved = committeeRepository.save(committee);
        logger.info("Committee registered successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Register new approver with encrypted password
     */
    public Approver registerApprover(Approver approver) {
        logger.info("Registering new approver: {}", approver.getName());
        
        if (approver.getPassword() == null || approver.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Encrypt password before saving
        approver.setPassword(passwordEncoder.encode(approver.getPassword()));
        Approver saved = approverRepository.save(approver);
        logger.info("Approver registered successfully with ID: {}", saved.getApproverId());
        return saved;
    }

    /**
     * Update committee password with encryption
     */
    public void updateCommitteePassword(Long id, String newPassword) {
        logger.info("Updating password for committee ID: {}", id);
        
        Committee committee = committeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Committee not found"));
        
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password is required");
        }

        committee.setPassword(passwordEncoder.encode(newPassword));
        committeeRepository.save(committee);
        logger.info("Password updated successfully for committee ID: {}", id);
    }

    /**
     * Update approver password with encryption
     */
    public void updateApproverPassword(Long id, String newPassword) {
        logger.info("Updating password for approver ID: {}", id);
        
        Approver approver = approverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));
        
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password is required");
        }

        approver.setPassword(passwordEncoder.encode(newPassword));
        approverRepository.save(approver);
        logger.info("Password updated successfully for approver ID: {}", id);
    }
}
