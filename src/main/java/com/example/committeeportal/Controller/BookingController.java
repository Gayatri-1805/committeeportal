package com.example.committeeportal.Controller;

import com.example.committeeportal.Entity.Booking;
import com.example.committeeportal.Entity.Event;
import com.example.committeeportal.Entity.Venue;
import com.example.committeeportal.Repository.BookingRepository;
import com.example.committeeportal.Repository.EventRepository;
import com.example.committeeportal.Repository.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private VenueRepository venueRepository;

    boolean isVenueAvailable(Venue venue, LocalDate date, LocalTime newStart, LocalTime newEnd) {
        List<Booking> bookings = bookingRepository.findBookingsByVenueAndDate(venue.getVenueId(), date);
        
        for (Booking b : bookings) {
            LocalTime existingStart = b.getStartTime();
            LocalTime existingEnd = b.getEndTime();
    
            // Check if new booking overlaps with existing booking
            if (newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd)) {
                logger.warn("Venue {} is not available on {} between {} - {}", 
                        venue.getVenueName(), date, newStart, newEnd);
                return false;
            }
        }
        logger.info("Venue {} is available on {} between {} - {}", 
                venue.getVenueName(), date, newStart, newEnd);
        return true;
    }
    
    @GetMapping
    public List<Booking> getAllBookings() {
        logger.info("Fetching all bookings");
        return bookingRepository.findAll();
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        logger.info("Fetching booking with ID: {}", id);
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            logger.debug("Found booking: {}", booking.get());
            return ResponseEntity.ok(booking.get());
        }
        logger.warn("Booking not found with ID: {}", id);
        return ResponseEntity.notFound().build();
    }

    @PostMapping
public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
    logger.info("Creating new booking for event: {}", booking.getEventName());
    try {
        if (booking.getVenue() != null && booking.getVenue().getVenueId() != null) {
            Optional<Venue> venueOpt = venueRepository.findById(booking.getVenue().getVenueId());
            if (venueOpt.isPresent()) {
                Venue venue = venueOpt.get();

                // Check if venue is available for the selected time slot
                if (!isVenueAvailable(venue, booking.getEventDate(), booking.getStartTime(), booking.getEndTime())) {
                    logger.warn("Venue already booked for requested slot: {}", venue.getVenueName());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Venue is already booked for this time slot");
                }

                booking.setVenue(venue);
                booking.setVenueName(venue.getVenueName());
                booking.setVenueLocation(venue.getVenueLocation());
            }
        }

        if (booking.getEvent() != null && booking.getEvent().getEventId() != null) {
            Optional<Event> eventOpt = eventRepository.findById(booking.getEvent().getEventId());
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                booking.setEvent(event);
                booking.setEventName(event.getEventName());

                if (booking.getVenue() != null) {
                    event.setVenue(booking.getVenue());
                    eventRepository.save(event);
                }
            }
        }
        
        return ResponseEntity.ok(bookingRepository.save(booking));
    } catch (Exception e) {
        logger.error("Error creating booking: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error creating booking: " + e.getMessage());
    }
}

    @PutMapping("/{id}")
public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
    logger.info("Updating booking with ID: {}", id);
    Optional<Booking> optionalBooking = bookingRepository.findById(id);
    if (optionalBooking.isPresent()) {
        Booking booking = optionalBooking.get();

        // Handle venue change if needed
        if (bookingDetails.getVenue() != null &&
            (booking.getVenue() == null || !bookingDetails.getVenue().getVenueId().equals(booking.getVenue().getVenueId()))) {

            Optional<Venue> newVenueOpt = venueRepository.findById(bookingDetails.getVenue().getVenueId());
            if (newVenueOpt.isPresent()) {
                Venue newVenue = newVenueOpt.get();

                // Check time slot availability
                if (!isVenueAvailable(newVenue, bookingDetails.getEventDate(), bookingDetails.getStartTime(), bookingDetails.getEndTime())) {
                    logger.warn("Failed to update booking: Venue not available");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Venue is already booked for this time slot");
                }

                booking.setVenue(newVenue);
                booking.setVenueName(newVenue.getVenueName());
                booking.setVenueLocation(newVenue.getVenueLocation());

                if (booking.getEvent() != null) {
                    Optional<Event> eventOpt = eventRepository.findById(booking.getEvent().getEventId());
                    if (eventOpt.isPresent()) {
                        Event event = eventOpt.get();
                        event.setVenue(newVenue);
                        eventRepository.save(event);
                    }
                }
            }
        }

        // Handle event update
        if (bookingDetails.getEvent() != null &&
            (booking.getEvent() == null || !bookingDetails.getEvent().getEventId().equals(booking.getEvent().getEventId()))) {

            Optional<Event> eventOpt = eventRepository.findById(bookingDetails.getEvent().getEventId());
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                booking.setEvent(event);
                booking.setEventName(event.getEventName());

                if (booking.getVenue() != null) {
                    event.setVenue(booking.getVenue());
                    eventRepository.save(event);
                }
            }
        }

        // Update other fields
        booking.setEventName(bookingDetails.getEventName());
        booking.setEventDescription(bookingDetails.getEventDescription());
        booking.setBookingDate(bookingDetails.getBookingDate());
        booking.setEventDate(bookingDetails.getEventDate());
        booking.setStartTime(bookingDetails.getStartTime());
        booking.setEndTime(bookingDetails.getEndTime());
        booking.setStatus(bookingDetails.getStatus());

        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Booking updated successfully: {}", updatedBooking.getBookingId());
        return ResponseEntity.ok(updatedBooking);
    }
    logger.warn("Booking not found for update: {}", id);
    return ResponseEntity.notFound().build();
}


    @PatchMapping("/{id}")
public ResponseEntity<Booking> partialUpdateBooking(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
    logger.info("Partially updating booking with ID: {}", id);
    Optional<Booking> optionalBooking = bookingRepository.findById(id);
    if (optionalBooking.isPresent()) {
        Booking booking = optionalBooking.get();

        if (updates.containsKey("eventName")) booking.setEventName((String) updates.get("eventName"));
        if (updates.containsKey("eventDescription")) booking.setEventDescription((String) updates.get("eventDescription"));
        if (updates.containsKey("venueName")) booking.setVenueName((String) updates.get("venueName"));
        if (updates.containsKey("venueLocation")) booking.setVenueLocation((String) updates.get("venueLocation"));
        if (updates.containsKey("bookingDate")) booking.setBookingDate(LocalDate.parse((String) updates.get("bookingDate")));
        if (updates.containsKey("eventDate")) booking.setEventDate(LocalDate.parse((String) updates.get("eventDate")));
        if (updates.containsKey("timeSlot")) {
            String timeSlot = (String) updates.get("timeSlot");
            String[] times = timeSlot.split("-");
            LocalTime newStart = LocalTime.parse(times[0].trim());
            LocalTime newEnd = LocalTime.parse(times[1].trim());

            // Check time slot availability before updating
            if (booking.getVenue() != null &&
                !isVenueAvailable(booking.getVenue(), booking.getEventDate(), newStart, newEnd)) {
                logger.warn("Venue not available for partial update on booking ID: {}", id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(null);
            }

            booking.setStartTime(newStart);
            booking.setEndTime(newEnd);
        }
        if (updates.containsKey("status")) booking.setStatus((String) updates.get("status"));

        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Partial update successful for booking ID: {}", id);
        return ResponseEntity.ok(updatedBooking);
    }
    logger.warn("Booking not found for partial update: {}", id);
    return ResponseEntity.notFound().build();
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        logger.info("Deleting booking with ID: {}", id);
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            // Make the venue available again if it exists
            if (booking.getVenue() != null) {
                Optional<Venue> venueOpt = venueRepository.findById(booking.getVenue().getVenueId());
                if (venueOpt.isPresent()) {
                    Venue venue = venueOpt.get();
                    venue.setAvailable(true);
                    venueRepository.save(venue);
                    logger.debug("Venue marked available again: {}", venue.getVenueName());
                    
                    // Remove venue reference from event if it exists
                    if (booking.getEvent() != null) {
                        Optional<Event> eventOpt = eventRepository.findById(booking.getEvent().getEventId());
                        if (eventOpt.isPresent()) {
                            Event event = eventOpt.get();
                            event.setVenue(null);
                            eventRepository.save(event);
                            logger.debug("Removed venue from event: {}", event.getEventName());
                        }
                    }
                }
            }
            
            bookingRepository.delete(booking);
            logger.info("Booking deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        }
        logger.warn("Booking not found for deletion: {}", id);
        return ResponseEntity.notFound().build();
    }
}