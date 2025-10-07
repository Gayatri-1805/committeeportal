package com.example.committeeportal.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "venue")
public class Venue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Long venueId;
    
    @Column(name = "venue_name", nullable = false)
    private String venueName;
    
    @Column(name = "venue_location")
    private String venueLocation;
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "available")
    private Boolean available = true;
    
    @Column(name = "facilities")
    private String facilities;
    
    // Getters and Setters
    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    
    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }
    
    public String getVenueLocation() { return venueLocation; }
    public void setVenueLocation(String venueLocation) { this.venueLocation = venueLocation; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    
    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
}
