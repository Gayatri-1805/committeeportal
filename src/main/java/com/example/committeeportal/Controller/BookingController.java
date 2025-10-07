package com.example.committeeportal.Controller;

import com.example.committeeportal.Entity.Booking;
import com.example.committeeportal.Repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (booking.isPresent()) {
            return ResponseEntity.ok(booking.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingRepository.save(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking bookingDetails) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setEventName(bookingDetails.getEventName());
            booking.setEventDescription(bookingDetails.getEventDescription());
            booking.setVenueName(bookingDetails.getVenueName());
            booking.setVenueLocation(bookingDetails.getVenueLocation());
            booking.setBookingDate(bookingDetails.getBookingDate());
            booking.setEventDate(bookingDetails.getEventDate());
            booking.setTimeSlot(bookingDetails.getTimeSlot());
            booking.setStatus(bookingDetails.getStatus());
            Booking updatedBooking = bookingRepository.save(booking);
            return ResponseEntity.ok(updatedBooking);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Booking> partialUpdateBooking(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            if (updates.containsKey("eventName")) {
                booking.setEventName((String) updates.get("eventName"));
            }
            if (updates.containsKey("eventDescription")) {
                booking.setEventDescription((String) updates.get("eventDescription"));
            }
            if (updates.containsKey("venueName")) {
                booking.setVenueName((String) updates.get("venueName"));
            }
            if (updates.containsKey("venueLocation")) {
                booking.setVenueLocation((String) updates.get("venueLocation"));
            }
            if (updates.containsKey("bookingDate")) {
                booking.setBookingDate(java.time.LocalDate.parse((String) updates.get("bookingDate")));
            }
            if (updates.containsKey("eventDate")) {
                booking.setEventDate(java.time.LocalDate.parse((String) updates.get("eventDate")));
            }
            if (updates.containsKey("timeSlot")) {
                booking.setTimeSlot((String) updates.get("timeSlot"));
            }
            if (updates.containsKey("status")) {
                booking.setStatus((String) updates.get("status"));
            }
            Booking updatedBooking = bookingRepository.save(booking);
            return ResponseEntity.ok(updatedBooking);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}