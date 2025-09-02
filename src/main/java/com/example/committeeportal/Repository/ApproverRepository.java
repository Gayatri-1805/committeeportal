package com.example.committeeportal.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.committeeportal.Entity.Approver;

@Repository
public interface ApproverRepository extends JpaRepository<Approver, Long> {
    
    // Explicitly define findById with overridden return type
    @Override
    Optional<Approver> findById(Long id);
    
    // Find by email (useful for login)
    Optional<Approver> findByEmail(String email);
    
    // Find by name containing a string (case insensitive)
    List<Approver> findByNameContainingIgnoreCase(String name);
    
    // Custom query example - find approvers by role
    @Query("SELECT a FROM Approver a WHERE LOWER(a.role) = LOWER(?1)")
    List<Approver> findByRoleName(String role);
}
