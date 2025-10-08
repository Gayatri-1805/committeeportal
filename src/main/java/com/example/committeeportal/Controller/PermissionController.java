package com.example.committeeportal.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.committeeportal.Entity.Approval;
import com.example.committeeportal.Entity.Approver;
import com.example.committeeportal.Entity.Event;
import com.example.committeeportal.Entity.PermissionApplication;
import com.example.committeeportal.Repository.ApprovalRepository;
import com.example.committeeportal.Repository.ApproverRepository;
import com.example.committeeportal.Repository.EventRepository;
import com.example.committeeportal.Repository.PermissionApplicationRepository;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class); // Logger
    private final PermissionApplicationRepository permissionRepo;
    private final ApprovalRepository approvalRepo;
    private final ApproverRepository approverRepo;
    private final EventRepository eventRepo;

    public PermissionController(PermissionApplicationRepository permissionRepo, ApprovalRepository approvalRepo, ApproverRepository approverRepo, EventRepository eventRepo) {
        this.permissionRepo = permissionRepo;
        this.approvalRepo = approvalRepo;
        this.approverRepo = approverRepo;
        this.eventRepo = eventRepo;
    }

    // Get all permission applications
    @GetMapping
    public List<PermissionApplication> getAllApplications() {
        logger.info("Fetching all permission applications");
         try {
            List<PermissionApplication> applications = permissionRepo.findAll();
            logger.info("Found {} applications", applications.size());
            return applications;
        } catch (Exception e) {
            logger.error("Error fetching permission applications", e);
            return List.of();
        }
    }

    // Get a permission application by its ID
    @GetMapping("/{id}")
    public PermissionApplication getApplicationById(@PathVariable Long id) {
        logger.info("Fetching permission application with ID {}", id);
         try {
            Optional<PermissionApplication> application = permissionRepo.findById(id);
            if (application.isPresent()) {
                return application.get();
            } else {
                logger.warn("Permission application with ID {} not found", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error fetching permission application with ID {}", id, e);
            return null;
        }
    }

    // Submit a new permission application for an event
    @PostMapping("/submit/{eventId}")
    public PermissionApplication submitApplication(@PathVariable Long eventId, @RequestBody PermissionApplication application) {
        logger.info("Submitting permission application for event ID {}", eventId);
        Optional<Event> event = eventRepo.findById(eventId);
        if (event.isPresent()) {
            application.setEvent(event.get());
            application.setUploadDate(LocalDate.now());
            application.setStatus("Submitted");
            return permissionRepo.save(application);
        }else{logger.warn("Event with ID {} not found. Cannot submit application.", eventId);
        return null;}
    }

    // Approve or reject a permission application
    @PostMapping("/{applicationId}/approve/{approverId}")
    public Approval approveApplication(@PathVariable Long applicationId, @PathVariable Long approverId, @RequestBody Approval approvalDetails) {
        Optional<PermissionApplication> applicationOpt = permissionRepo.findById(applicationId);
        logger.info("Approving/rejecting application ID {} by approver ID {}", applicationId, approverId);
        Optional<Approver> approverOpt = approverRepo.findById(approverId);

        if (applicationOpt.isPresent() && approverOpt.isPresent()) {
            PermissionApplication application = applicationOpt.get();
            application.setStatus(approvalDetails.getApprovalStatus()); // e.g., "Approved" or "Rejected"
            permissionRepo.save(application);
            logger.info("Application ID {} status updated to '{}'", applicationId, approvalDetails.getApprovalStatus());

            approvalDetails.setPermissionApplication(application);
            approvalDetails.setApprover(approverOpt.get());
            approvalDetails.setApprovalDate(LocalDate.now());
            return approvalRepo.save(approvalDetails);
            
        }else{
                if (applicationOpt.isEmpty()) logger.warn("Application ID {} not found", applicationId);
                if (approverOpt.isEmpty()) logger.warn("Approver ID {} not found", approverId);
                return null;
        }
        
    }
}