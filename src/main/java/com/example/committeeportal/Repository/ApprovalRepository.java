package com.example.committeeportal.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.committeeportal.Entity.Approval;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}