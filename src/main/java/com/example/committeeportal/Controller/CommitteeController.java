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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import com.example.committeeportal.Entity.Committee;
import com.example.committeeportal.Repository.CommitteeRepository;

@RestController
@RequestMapping("/api/committees")
public class CommitteeController {
    
    @Autowired
    private CommitteeRepository committeeRepository;
    
    // Get all committees
    @GetMapping
    public ResponseEntity<List<Committee>> getAllCommittees() {
        try {
            List<Committee> committees = committeeRepository.findAll();
            
            if (committees.isEmpty()) {
                return ResponseEntity.ok(committees);
            }
            
            return ResponseEntity.ok(committees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get committee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Committee> getCommitteeById(@PathVariable Long id) {
        try {
            Optional<Committee> committeeData = committeeRepository.findById(id);
            
            if (committeeData.isPresent()) {
                return ResponseEntity.ok(committeeData.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Create a new committee
    @PostMapping
    public ResponseEntity<Committee> createCommittee(@RequestBody Committee committee) {
        try {
            // Check if committee name already exists
            if (committeeRepository.existsByCommitteeNameIgnoreCase(committee.getCommitteeName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            // Check if email already exists
            if (committee.getContactEmail() != null && 
                committeeRepository.existsByContactEmailIgnoreCase(committee.getContactEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            Committee savedCommittee = committeeRepository.save(committee);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCommittee);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update an existing committee
    @PutMapping("/{id}")
    public ResponseEntity<Committee> updateCommittee(
            @PathVariable Long id, @RequestBody Committee committee) {
        try {
            Optional<Committee> committeeData = committeeRepository.findById(id);
            
            if (committeeData.isPresent()) {
                Committee existingCommittee = committeeData.get();
                
                // Update fields if they are not null in the request
                if (committee.getCommitteeName() != null) {
                    existingCommittee.setCommitteeName(committee.getCommitteeName());
                }
                if (committee.getHeadOfCommittee() != null) {
                    existingCommittee.setHeadOfCommittee(committee.getHeadOfCommittee());
                }
                if (committee.getContactEmail() != null) {
                    existingCommittee.setContactEmail(committee.getContactEmail());
                }
                if (committee.getPassword() != null) {
                    existingCommittee.setPassword(committee.getPassword());
                }
                
                Committee updatedCommittee = committeeRepository.save(existingCommittee);
                return ResponseEntity.ok(updatedCommittee);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Delete a committee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommittee(@PathVariable Long id) {
        try {
            Optional<Committee> committeeData = committeeRepository.findById(id);
            
            if (committeeData.isPresent()) {
                committeeRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Search committees by name
    @GetMapping("/search")
    public ResponseEntity<List<Committee>> searchCommitteesByName(
            @RequestParam("name") String name) {
        try {
            List<Committee> committees = committeeRepository.findByCommitteeNameContainingIgnoreCase(name);
            return ResponseEntity.ok(committees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<Committee> login(@RequestBody Committee loginRequest) {
        try {
            if (loginRequest.getContactEmail() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            Committee committee = committeeRepository.findByContactEmailIgnoreCase(loginRequest.getContactEmail());
            
            if (committee != null && committee.getPassword() != null && 
                committee.getPassword().equals(loginRequest.getPassword())) {
                // Password matches
                return ResponseEntity.ok(committee);
            } else {
                // Invalid credentials
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
  

@PatchMapping("/{id}")
public ResponseEntity<Committee> patchCommittee(
        @PathVariable Long id, @RequestBody Committee committee) {
    try {
        Optional<Committee> committeeData = committeeRepository.findById(id);
        
        if (committeeData.isPresent()) {
            Committee existingCommittee = committeeData.get();
            
            // Update only provided (non-null) fields
            if (committee.getCommitteeName() != null) {
                existingCommittee.setCommitteeName(committee.getCommitteeName());
            }
            if (committee.getHeadOfCommittee() != null) {
                existingCommittee.setHeadOfCommittee(committee.getHeadOfCommittee());
            }
            if (committee.getContactEmail() != null) {
                existingCommittee.setContactEmail(committee.getContactEmail());
            }
            if (committee.getPassword() != null) {
                existingCommittee.setPassword(committee.getPassword());
            }
            
            Committee updatedCommittee = committeeRepository.save(existingCommittee);
            return ResponseEntity.ok(updatedCommittee);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    
}
