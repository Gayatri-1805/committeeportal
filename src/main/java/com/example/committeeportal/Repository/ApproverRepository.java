package com.example.committeeportal.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.committeeportal.Entity.Approver;


@Repository
public interface ApproverRepository extends JpaRepository<Approver, Long> {
}

