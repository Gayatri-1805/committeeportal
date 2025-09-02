package com.example.committeeportal.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.committeeportal.Entity.Committee;
import com.example.committeeportal.Repository.CommitteeRepository;

@RestController
@RequestMapping("/api/committees") // Note the new URL path
public class CommitteeController {
    
    @Autowired
    private CommitteeRepository committeeRepository;
    
    // Get all committees
    @GetMapping
    public ResponseEntity<List<Committee>> getAllCommittees() {
        List<Committee> committees = committeeRepository.findAll();
        return ResponseEntity.ok(committees);
    }
    
    // Get committee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Committee> getCommitteeById(@PathVariable Long id) {
        Optional<Committee> committeeData = committeeRepository.findById(id);
        return committeeData.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    // Create a new committee
    @PostMapping
    public ResponseEntity<Committee> createCommittee(@RequestBody Committee committee) {
        if (committee.getCommitteeName() != null && committeeRepository.existsByCommitteeNameIgnoreCase(committee.getCommitteeName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (committee.getContactEmail() != null && committeeRepository.existsByContactEmailIgnoreCase(committee.getContactEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Committee savedCommittee = committeeRepository.save(committee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCommittee);
    }
    
    // Update an existing committee (full update)
    @PutMapping("/{id}")
    public ResponseEntity<Committee> updateCommittee(@PathVariable Long id, @RequestBody Committee committeeDetails) {
        return committeeRepository.findById(id).map(existingCommittee -> {
            existingCommittee.setCommitteeName(committeeDetails.getCommitteeName());
            existingCommittee.setHeadOfCommittee(committeeDetails.getHeadOfCommittee());
            existingCommittee.setContactEmail(committeeDetails.getContactEmail());
            existingCommittee.setPassword(committeeDetails.getPassword());
            Committee updatedCommittee = committeeRepository.save(existingCommittee);
            return ResponseEntity.ok(updatedCommittee);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    // Delete a committee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommittee(@PathVariable Long id) {
        if (committeeRepository.existsById(id)) {
            committeeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<Committee> login(@RequestBody Committee loginRequest) {
        Committee committee = committeeRepository.findByContactEmailIgnoreCase(loginRequest.getContactEmail());
        if (committee != null && committee.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(committee);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}