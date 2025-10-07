package com.example.committeeportal.Repository;

import com.example.committeeportal.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Custom query methods can be added here if needed
}