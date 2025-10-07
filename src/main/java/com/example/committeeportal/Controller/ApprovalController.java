package com.example.committeeportal.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.committeeportal.Entity.Approval;
import com.example.committeeportal.Entity.Approver;
import com.example.committeeportal.Entity.PermissionApplication;
import com.example.committeeportal.Repository.ApprovalRepository;
import com.example.committeeportal.Repository.ApproverRepository;
import com.example.committeeportal.Repository.PermissionApplicationRepository;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {
    
    @Autowired
    private ApprovalRepository approvalRepository;
    
    @Autowired
    private ApproverRepository approverRepository;
    
    @Autowired
    private PermissionApplicationRepository permissionApplicationRepository;
    
    // GET all approvals
    @GetMapping
    public ResponseEntity<List<Approval>> getAllApprovals() {
        try {
            List<Approval> approvals = approvalRepository.findAll();
            return ResponseEntity.ok(approvals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET approval by ID
    @GetMapping("/{id}")
    public ResponseEntity<Approval> getApprovalById(@PathVariable Long id) {
        try {
            Optional<Approval> approvalData = approvalRepository.findById(id);
            
            if (approvalData.isPresent()) {
                return ResponseEntity.ok(approvalData.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // POST create new approval
    @PostMapping
    public ResponseEntity<Approval> createApproval(@RequestBody Approval approval) {
        try {
            // Validate that referenced entities exist
            if (approval.getApprover() != null && approval.getApprover().getApproverId() != null) {
                Optional<Approver> approver = approverRepository.findById(approval.getApprover().getApproverId());
                if (approver.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                approval.setApprover(approver.get());
            }
            
            if (approval.getPermissionApplication() != null && approval.getPermissionApplication().getApplicationId() != null) {
                Optional<PermissionApplication> permissionApp = permissionApplicationRepository.findById(
                    approval.getPermissionApplication().getApplicationId());
                if (permissionApp.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                approval.setPermissionApplication(permissionApp.get());
            }
            
            // Set approval date if not provided
            if (approval.getApprovalDate() == null) {
                approval.setApprovalDate(LocalDate.now());
            }
            
            Approval savedApproval = approvalRepository.save(approval);
            return new ResponseEntity<>(savedApproval, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // POST create approval for a specific permission application by a specific approver
    @PostMapping("/{applicationId}/approver/{approverId}")
    public ResponseEntity<Approval> createApprovalForApplication(
            @PathVariable Long applicationId,
            @PathVariable Long approverId,
            @RequestBody Approval approvalDetails) {
        try {
            Optional<PermissionApplication> applicationOpt = permissionApplicationRepository.findById(applicationId);
            Optional<Approver> approverOpt = approverRepository.findById(approverId);
            
            if (applicationOpt.isEmpty() || approverOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            PermissionApplication application = applicationOpt.get();
            Approver approver = approverOpt.get();
            
            // Update application status
            application.setStatus(approvalDetails.getApprovalStatus());
            permissionApplicationRepository.save(application);
            
            // Create approval record
            Approval approval = new Approval();
            approval.setPermissionApplication(application);
            approval.setApprover(approver);
            approval.setApprovalStatus(approvalDetails.getApprovalStatus());
            approval.setRemarks(approvalDetails.getRemarks());
            approval.setApprovalDate(LocalDate.now());
            
            Approval savedApproval = approvalRepository.save(approval);
            return new ResponseEntity<>(savedApproval, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PUT update approval
    @PutMapping("/{id}")
    public ResponseEntity<Approval> updateApproval(@PathVariable Long id, @RequestBody Approval approval) {
        try {
            Optional<Approval> approvalData = approvalRepository.findById(id);
            
            if (approvalData.isPresent()) {
                Approval existingApproval = approvalData.get();
                
                // Update the fields
                existingApproval.setApprovalStatus(approval.getApprovalStatus());
                existingApproval.setRemarks(approval.getRemarks());
                
                // Only update these if they're provided and valid
                if (approval.getApprover() != null && approval.getApprover().getApproverId() != null) {
                    Optional<Approver> approver = approverRepository.findById(approval.getApprover().getApproverId());
                    if (approver.isPresent()) {
                        existingApproval.setApprover(approver.get());
                    }
                }
                
                if (approval.getPermissionApplication() != null && approval.getPermissionApplication().getApplicationId() != null) {
                    Optional<PermissionApplication> permissionApp = permissionApplicationRepository.findById(
                        approval.getPermissionApplication().getApplicationId());
                    if (permissionApp.isPresent()) {
                        existingApproval.setPermissionApplication(permissionApp.get());
                    }
                }
                
                if (approval.getApprovalDate() != null) {
                    existingApproval.setApprovalDate(approval.getApprovalDate());
                }
                
                Approval updatedApproval = approvalRepository.save(existingApproval);
                return ResponseEntity.ok(updatedApproval);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PATCH partial update approval
    @PatchMapping("/{id}")
    public ResponseEntity<Approval> patchApproval(@PathVariable Long id, @RequestBody Approval approval) {
        try {
            Optional<Approval> approvalData = approvalRepository.findById(id);
            
            if (approvalData.isPresent()) {
                Approval existingApproval = approvalData.get();
                
                // Only update fields that are provided
                if (approval.getApprovalStatus() != null) {
                    existingApproval.setApprovalStatus(approval.getApprovalStatus());
                }
                
                if (approval.getRemarks() != null) {
                    existingApproval.setRemarks(approval.getRemarks());
                }
                
                if (approval.getApprover() != null && approval.getApprover().getApproverId() != null) {
                    Optional<Approver> approver = approverRepository.findById(approval.getApprover().getApproverId());
                    if (approver.isPresent()) {
                        existingApproval.setApprover(approver.get());
                    }
                }
                
                if (approval.getPermissionApplication() != null && approval.getPermissionApplication().getApplicationId() != null) {
                    Optional<PermissionApplication> permissionApp = permissionApplicationRepository.findById(
                        approval.getPermissionApplication().getApplicationId());
                    if (permissionApp.isPresent()) {
                        existingApproval.setPermissionApplication(permissionApp.get());
                    }
                }
                
                if (approval.getApprovalDate() != null) {
                    existingApproval.setApprovalDate(approval.getApprovalDate());
                }
                
                Approval updatedApproval = approvalRepository.save(existingApproval);
                return ResponseEntity.ok(updatedApproval);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // DELETE approval
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApproval(@PathVariable Long id) {
        try {
            Optional<Approval> approvalData = approvalRepository.findById(id);
            
            if (approvalData.isPresent()) {
                approvalRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET all approvals for a specific permission application
    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Approval>> getApprovalsByApplication(@PathVariable Long applicationId) {
        try {
            // Check if the application exists
            if (!permissionApplicationRepository.existsById(applicationId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Use the repository method we added
            List<Approval> approvals = approvalRepository.findByPermissionApplication_ApplicationId(applicationId);
            return ResponseEntity.ok(approvals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET all approvals by a specific approver
    @GetMapping("/approver/{approverId}")
    public ResponseEntity<List<Approval>> getApprovalsByApprover(@PathVariable Long approverId) {
        try {
            // Check if the approver exists
            if (!approverRepository.existsById(approverId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Use the repository method we added
            List<Approval> approvals = approvalRepository.findByApprover_ApproverId(approverId);
            return ResponseEntity.ok(approvals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET all approvals by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Approval>> getApprovalsByStatus(@PathVariable String status) {
        try {
            List<Approval> approvals = approvalRepository.findByApprovalStatus(status);
            return ResponseEntity.ok(approvals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
