package com.example.committeeportal.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.committeeportal.Entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{
    // 1. Find all events by status (e.g., "Scheduled", "Completed", "Cancelled")
    List<Event> findByStatus(String status);

    // 2. Find all events by committeeId (assuming Event has a committee field with id)
    List<Event> findByCommittee_Id(Long committeeId);

    // 3. Find events happening on a particular date
    List<Event> findByEventDate(LocalDate eventDate);

    // 4. Find events created after a specific date
    List<Event> findByCreatedDateAfter(LocalDate date);

    // 5. Find events with expected participants greater than a number
    List<Event> findByExpectedParticipantsGreaterThan(Integer count);

    // 6. Combine conditions: Find by status and date
    List<Event> findByStatusAndEventDate(String status, LocalDate eventDate);
}

