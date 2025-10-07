package com.example.committeeportal.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.committeeportal.Entity.Venue;
import com.example.committeeportal.Repository.VenueRepository;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    
    @Autowired
    private VenueRepository venueRepository;
    
    // GET - Get all venues
    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        try {
            List<Venue> venues = venueRepository.findAll();
            return ResponseEntity.ok(venues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET - Get venue by ID
    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        try {
            Optional<Venue> venueData = venueRepository.findById(id);
            
            if (venueData.isPresent()) {
                return ResponseEntity.ok(venueData.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // POST - Create a new venue
    @PostMapping
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        try {
            // Check if venue name already exists
            if (venueRepository.existsByVenueNameIgnoreCase(venue.getVenueName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            Venue savedVenue = venueRepository.save(venue);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVenue);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PUT - Update an existing venue
    @PutMapping("/{id}")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody Venue venue) {
        try {
            Optional<Venue> venueData = venueRepository.findById(id);
            
            if (venueData.isPresent()) {
                Venue existingVenue = venueData.get();
                
                // Update all fields
                existingVenue.setVenueName(venue.getVenueName());
                existingVenue.setVenueLocation(venue.getVenueLocation());
                existingVenue.setCapacity(venue.getCapacity());
                existingVenue.setAvailable(venue.getAvailable());
                existingVenue.setFacilities(venue.getFacilities());
                
                Venue updatedVenue = venueRepository.save(existingVenue);
                return ResponseEntity.ok(updatedVenue);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // PATCH - Partially update an existing venue
    @PatchMapping("/{id}")
    public ResponseEntity<Venue> patchVenue(@PathVariable Long id, @RequestBody Venue venue) {
        try {
            Optional<Venue> venueData = venueRepository.findById(id);
            
            if (venueData.isPresent()) {
                Venue existingVenue = venueData.get();
                
                // Update only provided (non-null) fields
                if (venue.getVenueName() != null) {
                    existingVenue.setVenueName(venue.getVenueName());
                }
                if (venue.getVenueLocation() != null) {
                    existingVenue.setVenueLocation(venue.getVenueLocation());
                }
                if (venue.getCapacity() != null) {
                    existingVenue.setCapacity(venue.getCapacity());
                }
                if (venue.getAvailable() != null) {
                    existingVenue.setAvailable(venue.getAvailable());
                }
                if (venue.getFacilities() != null) {
                    existingVenue.setFacilities(venue.getFacilities());
                }
                
                Venue updatedVenue = venueRepository.save(existingVenue);
                return ResponseEntity.ok(updatedVenue);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // DELETE - Delete a venue
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        try {
            Optional<Venue> venueData = venueRepository.findById(id);
            
            if (venueData.isPresent()) {
                venueRepository.deleteById(id);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET - Search venues by name
    @GetMapping("/search/name")
    public ResponseEntity<List<Venue>> searchVenuesByName(@RequestParam String name) {
        try {
            List<Venue> venues = venueRepository.findByVenueNameContainingIgnoreCase(name);
            return ResponseEntity.ok(venues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET - Search venues by location
    @GetMapping("/search/location")
    public ResponseEntity<List<Venue>> searchVenuesByLocation(@RequestParam String location) {
        try {
            List<Venue> venues = venueRepository.findByVenueLocationContainingIgnoreCase(location);
            return ResponseEntity.ok(venues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET - Get available venues
    @GetMapping("/available")
    public ResponseEntity<List<Venue>> getAvailableVenues() {
        try {
            List<Venue> venues = venueRepository.findByAvailable(true);
            return ResponseEntity.ok(venues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET - Get venues with minimum capacity
    @GetMapping("/capacity")
    public ResponseEntity<List<Venue>> getVenuesByCapacity(@RequestParam Integer minCapacity) {
        try {
            List<Venue> venues = venueRepository.findByCapacityGreaterThanEqual(minCapacity);
            return ResponseEntity.ok(venues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
