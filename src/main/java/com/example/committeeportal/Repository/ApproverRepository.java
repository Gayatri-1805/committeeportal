package com.example.committeeportal.Repository;

import com.example.committeeportal.Entity.Approver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApproverRepository extends JpaRepository<Approver, Long> {
}
