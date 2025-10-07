package com.example.committeeportal.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "permission_application")
public class PermissionApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private Event event;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @Column(name = "permission_doc")
    private String permissionDoc;

    @Column(name = "status")
    private String status;

    // Getters and Setters
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }

    public String getPermissionDoc() { return permissionDoc; }
    public void setPermissionDoc(String permissionDoc) { this.permissionDoc = permissionDoc; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}