    package com.example.committeeportal.Controller;

    import java.util.List;
    import java.util.Optional;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory; 

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
         private static final Logger logger = LoggerFactory.getLogger(CommitteeController.class);
        
        @Autowired
        private CommitteeRepository committeeRepository;
        
        // Get all committees
        @GetMapping 
        public ResponseEntity<List<Committee>> getAllCommittees() {
            logger.info("Fetching all committees");
            try {
                List<Committee> committees = committeeRepository.findAll();
                
                if (committees.isEmpty()) {
                    logger.info("No committees found");
                    return ResponseEntity.ok(committees);
                }
                logger.info("Found {} committees", committees.size());
                return ResponseEntity.ok(committees);
            } catch (Exception e) {
                logger.error("Error fetching committees", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        // Get committee by ID
        @GetMapping("/{id}")
        public ResponseEntity<Committee> getCommitteeById(@PathVariable Long id) {
            logger.info("Fetching committee with ID {}", id);
            try {
                Optional<Committee> committeeData = committeeRepository.findById(id);
                
                if (committeeData.isPresent()) {
                    return ResponseEntity.ok(committeeData.get());
                } else {
                    logger.warn("Committee with ID {} not found", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } catch (Exception e) {
                logger.error("Error fetching committee with ID {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        // Create a new committee
        @PostMapping
        public ResponseEntity<Committee> createCommittee(@RequestBody Committee committee) {
            logger.info("Creating new committee: {}", committee.getCommitteeName());
            try {
                // Check if committee name already exists
                if (committeeRepository.existsByCommitteeNameIgnoreCase(committee.getCommitteeName())) {
                    logger.warn("Committee name {} already exists", committee.getCommitteeName());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                
                // Check if email already exists
                if (committee.getContactEmail() != null && 
                    committeeRepository.existsByContactEmailIgnoreCase(committee.getContactEmail())) {
                    logger.warn("Email {} already exists", committee.getContactEmail());
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                
                Committee savedCommittee = committeeRepository.save(committee);
                logger.info("Committee {} created successfully with ID {}", savedCommittee.getCommitteeName(), savedCommittee.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(savedCommittee);
            } catch (Exception e) {
                logger.error("Error creating committee {}", committee.getCommitteeName(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        // Update an existing committee
        @PutMapping("/{id}")
        public ResponseEntity<Committee> updateCommittee(
            
                @PathVariable Long id, @RequestBody Committee committee) {
                logger.info("Updating committee with ID {}", id); 
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
                    logger.info("Committee with ID {} updated successfully", id);

                    return ResponseEntity.ok(updatedCommittee);
                } else {
                    logger.warn("Committee with ID {} not found", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } catch (Exception e) {
                logger.error("Error updating committee with ID {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        // Delete a committee
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCommittee(@PathVariable Long id) {
            logger.info("Deleting committee with ID {}", id);
            try {
                Optional<Committee> committeeData = committeeRepository.findById(id);
                
                if (committeeData.isPresent()) {
                    committeeRepository.deleteById(id);
                    logger.info("Committee with ID {} deleted successfully", id);
                    return ResponseEntity.ok().build();
                } else {
                    logger.warn("Committee with ID {} not found", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } catch (Exception e) {
                logger.error("Error deleting committee with ID {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        // Search committees by name
        @GetMapping("/search")
        public ResponseEntity<List<Committee>> searchCommitteesByName(
                
                @RequestParam("name") String name) {
                logger.info("Searching committees with name containing '{}'", name);
            try {
                List<Committee> committees = committeeRepository.findByCommitteeNameContainingIgnoreCase(name);
                logger.info("Found {} committees matching '{}'", committees.size(), name);
                return ResponseEntity.ok(committees);
            } catch (Exception e) {
                logger.error("Error searching committees with name containing '{}'", name, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        // Login endpoint
        @PostMapping("/login")
        public ResponseEntity<Committee> login(@RequestBody Committee loginRequest) {
            logger.info("Login attempt for email: {}", loginRequest.getContactEmail()); 
            try {
                if (loginRequest.getContactEmail() == null || loginRequest.getPassword() == null) {
                    logger.warn("Login failed: missing email or password");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                
                Committee committee = committeeRepository.findByContactEmailIgnoreCase(loginRequest.getContactEmail());
                
                if (committee != null && committee.getPassword() != null && 
                    committee.getPassword().equals(loginRequest.getPassword())) {
                    logger.info("Login successful for email: {}", loginRequest.getContactEmail());
                        // Password matches
                    return ResponseEntity.ok(committee);
                } else {
                    // Invalid credentials
                    logger.warn("Login failed for email: {}", loginRequest.getContactEmail());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } catch (Exception e) {
                logger.error("Error during login for email {}", loginRequest.getContactEmail(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        
    

    @PatchMapping("/{id}")
    public ResponseEntity<Committee> patchCommittee(
            @PathVariable Long id, @RequestBody Committee committee) {
                logger.info("Patching committee with ID {}", id);
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
                logger.info("Committee with ID {} patched successfully", id);
                return ResponseEntity.ok(updatedCommittee);
            } else {
                logger.warn("Committee with ID {} not found", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error("Error patching committee with ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

        
    }