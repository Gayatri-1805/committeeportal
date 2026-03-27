package com.example.committeeportal.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.committeeportal.Entity.Venue;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    
    // Find venue by name (case-insensitive)
    List<Venue> findByVenueNameContainingIgnoreCase(String name);
    
    // Find venue by location (case-insensitive)
    List<Venue> findByVenueLocationContainingIgnoreCase(String location);
    
    // Find venues by availability
    List<Venue> findByAvailable(Boolean available);
    
    // Find venues with minimum capacity
    List<Venue> findByCapacityGreaterThanEqual(Integer minCapacity);
    
    // Check if venue name already exists
    boolean existsByVenueNameIgnoreCase(String venueName);
}
