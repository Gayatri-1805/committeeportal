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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "permission", description = "Operations related to permissions")
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
    @Operation(summary = "Get all permission")
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
    @Operation(summary = "Get all permission by id")
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

    // Get all permission applications targeted at a specific approver
    @Operation(summary = "Get permission applications by approver id")
    @GetMapping("/approver/{approverId}")
    public List<PermissionApplication> getApplicationsByApprover(@PathVariable Long approverId) {
        logger.info("Fetching permission applications for approver ID {}", approverId);
        try {
            List<PermissionApplication> applications = permissionRepo.findByApprover_ApproverId(approverId);
            logger.info("Found {} targeted applications", applications.size());
            return applications;
        } catch (Exception e) {
            logger.error("Error fetching targeted applications", e);
            return List.of();
        }
    }

    // Submit a new permission application for an event to a specific approver
    @PostMapping("/submit/{eventId}/{approverId}")
    public PermissionApplication submitApplication(@PathVariable Long eventId, @PathVariable Long approverId, @RequestBody PermissionApplication application) {
        logger.info("Submitting permission application for event ID {} to approver ID {}", eventId, approverId);
        Optional<Event> eventOpt = eventRepo.findById(eventId);
        Optional<Approver> approverOpt = approverRepo.findById(approverId);
        
        if (eventOpt.isPresent() && approverOpt.isPresent()) {
            Event event = eventOpt.get();
            application.setEvent(event);
            application.setApprover(approverOpt.get());
            application.setUploadDate(LocalDate.now());
            application.setStatus("Submitted");
            
            // Sync status with Event
            event.setStatus("Pending"); 
            eventRepo.save(event);
            
            return permissionRepo.save(application);
        } else {
            logger.warn("Event with ID {} not found. Cannot submit application.", eventId);
            return null;
        }
    }

    // Approve or reject a permission application
    @Operation(summary = "approve or reject a new permission")
    @PostMapping("/{applicationId}/approve/{approverId}")
    public Approval approveApplication(@PathVariable Long applicationId, @PathVariable Long approverId, @RequestBody Approval approvalDetails) {
        Optional<PermissionApplication> applicationOpt = permissionRepo.findById(applicationId);
        logger.info("Approving/rejecting application ID {} by approver ID {}", applicationId, approverId);
        Optional<Approver> approverOpt = approverRepo.findById(approverId);

        if (applicationOpt.isPresent() && approverOpt.isPresent()) {
            PermissionApplication application = applicationOpt.get();
            String newStatus = approvalDetails.getApprovalStatus(); // e.g., "Approved" or "Rejected"
            application.setStatus(newStatus); 
            permissionRepo.save(application);
            logger.info("Application ID {} status updated to '{}'", applicationId, newStatus);

            // Sync status with Event
            Event event = application.getEvent();
            if (event != null) {
                event.setStatus(newStatus);
                eventRepo.save(event);
                logger.info("Event ID {} status synchronized to '{}'", event.getEventId(), newStatus);
            }

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