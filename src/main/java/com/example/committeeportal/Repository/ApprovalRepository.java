package com.example.committeeportal.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.committeeportal.Entity.Approval;
import com.example.committeeportal.Entity.Approver;
import com.example.committeeportal.Entity.PermissionApplication;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval,Long> {
    
    // Find approvals by permission application
    List<Approval> findByPermissionApplication(PermissionApplication permissionApplication);
    
    // Find approvals by application ID
    List<Approval> findByPermissionApplication_ApplicationId(Long applicationId);
    
    // Find approvals by approver
    List<Approval> findByApprover(Approver approver);
    
    // Find approvals by approver ID
    List<Approval> findByApprover_ApproverId(Long approverId);
    
    // Find approvals by status
    List<Approval> findByApprovalStatus(String status);
}
