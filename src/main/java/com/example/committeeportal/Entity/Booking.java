package com.example.committeeportal.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "booking")
public class Booking {

    private LocalTime startTime;
private LocalTime endTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private String eventName;
    private String eventDescription;

    private String venueName;
    private String venueLocation;

    private LocalDate bookingDate;
    private LocalDate eventDate;
    
    private String status;
    
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getVenueName() {
        return venueName;
    }
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueLocation() {
        return venueLocation;
    }
    public void setVenueLocation(String venueLocation) {
        this.venueLocation = venueLocation;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }
    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

  public LocalTime getStartTime() {
    return startTime;
}

public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
}

public LocalTime getEndTime() {
    return endTime;
}

public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
}


    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public Venue getVenue() {
        return venue;
    }
    public void setVenue(Venue venue) {
        this.venue = venue;
    }
}