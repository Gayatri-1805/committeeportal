package com.example.committeeportal.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        return permissionRepo.findAll();
    }

    // Get a permission application by its ID
    @GetMapping("/{id}")
    public PermissionApplication getApplicationById(@PathVariable Long id) {
        return permissionRepo.findById(id).orElse(null);
    }

    // Submit a new permission application for an event
    @PostMapping("/submit/{eventId}")
    public PermissionApplication submitApplication(@PathVariable Long eventId, @RequestBody PermissionApplication application) {
        Optional<Event> event = eventRepo.findById(eventId);
        if (event.isPresent()) {
            application.setEvent(event.get());
            application.setUploadDate(LocalDate.now());
            application.setStatus("Submitted");
            return permissionRepo.save(application);
        }
        return null;
    }

    // Approve or reject a permission application
    @PostMapping("/{applicationId}/approve/{approverId}")
    public Approval approveApplication(@PathVariable Long applicationId, @PathVariable Long approverId, @RequestBody Approval approvalDetails) {
        Optional<PermissionApplication> applicationOpt = permissionRepo.findById(applicationId);
        Optional<Approver> approverOpt = approverRepo.findById(approverId);

        if (applicationOpt.isPresent() && approverOpt.isPresent()) {
            PermissionApplication application = applicationOpt.get();
            application.setStatus(approvalDetails.getApprovalStatus()); // e.g., "Approved" or "Rejected"
            permissionRepo.save(application);

            approvalDetails.setPermissionApplication(application);
            approvalDetails.setApprover(approverOpt.get());
            approvalDetails.setApprovalDate(LocalDate.now());
            return approvalRepo.save(approvalDetails);
        }
        return null;
    }
}