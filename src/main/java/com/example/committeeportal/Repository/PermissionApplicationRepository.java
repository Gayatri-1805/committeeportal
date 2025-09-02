package com.example.committeeportal.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.committeeportal.Entity.PermissionApplication;

@Repository
public interface PermissionApplicationRepository extends JpaRepository<PermissionApplication, Long> {
}