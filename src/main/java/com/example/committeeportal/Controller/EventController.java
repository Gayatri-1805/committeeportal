package com.example.committeeportal.Controller;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.committeeportal.Entity.Event;
import com.example.committeeportal.Repository.EventRepository;


@RestController
@RequestMapping("/events")
public class EventController{
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
//------Basic CRUD operations for Event entity------//
    //Get all events
    @GetMapping
    public List<Event> getAllEvents() {
        logger.info("Fetching all events...");
        return eventRepository.findAll();
    }

    //Get event by id
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        logger.info("Fetching event with ID: {}", id);
        return eventRepository.findById(id).orElse(null);
    }
    
    //Create new event
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        logger.info("Creating new event: {}", event.getEventName());
        return eventRepository.save(event);
    }
    
    //update full event
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event updatedEvent) {
        logger.info("Updating event with ID: {}", id);
        return eventRepository.findById(id).map(event -> {
            event.setEventName(updatedEvent.getEventName());
            event.setEventDate(updatedEvent.getEventDate());
            event.setExpectedParticipants(updatedEvent.getExpectedParticipants());
            event.setCreatedDate(updatedEvent.getCreatedDate());
            event.setStatus(updatedEvent.getStatus());
            event.setCommittee(updatedEvent.getCommittee());
            logger.info("Event {} updated successfully", id);
            return eventRepository.save(event);
        }).orElse(null);
    }
    
    //update partial event
    @PatchMapping("/{id}")
    public Event partiallyUpdateEvent(@PathVariable Long id, @RequestBody Event partialEvent) {
        logger.info("Partially updating event with ID: {}", id);
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isPresent()) {
            Event event = existingEvent.get();
            if (partialEvent.getEventName() != null) event.setEventName(partialEvent.getEventName());
            if (partialEvent.getEventDate() != null) event.setEventDate(partialEvent.getEventDate());
            if (partialEvent.getExpectedParticipants() != null) event.setExpectedParticipants(partialEvent.getExpectedParticipants());
            if (partialEvent.getCreatedDate() != null) event.setCreatedDate(partialEvent.getCreatedDate());
            if (partialEvent.getStatus() != null) event.setStatus(partialEvent.getStatus());
            if (partialEvent.getCommittee() != null) event.setCommittee(partialEvent.getCommittee());
            logger.info("Event {} partially updated", id);
            return eventRepository.save(event);
        }
        logger.warn("Event with ID {} not found for partial update!", id);
        return null;
    }    
    
    //Delete event by id
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {
        logger.info("Deleting event with ID: {}", id);
        if(eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            logger.info("Event {} deleted successfully", id);
            return "Event deleted successfully!";
        } else {
            logger.warn("Event with ID {} not found for deletion!", id);
            return "Event not found!";
        }
    }

// -----------------Custom Queries-----------------//
   // Get events by status
    @GetMapping("/status/{status}")
    public List<Event> getEventsByStatus(@PathVariable String status) {
        logger.info("Fetching events with status: {}", status);
        return eventRepository.findByStatus(status);
    }

    // Get events by committeeId
    @GetMapping("/committee/{committeeId}")
    public List<Event> getEventsByCommittee(@PathVariable Long committeeId) {
        logger.info("Fetching events for committee ID: {}", committeeId);
        return eventRepository.findByCommittee_Id(committeeId);
    }

    // Get events by date (format: yyyy-MM-dd)
    @GetMapping("/date/{eventDate}")
    public List<Event> getEventsByDate(@PathVariable String eventDate) {
        logger.info("Fetching events for date: {}", eventDate);
        LocalDate date = LocalDate.parse(eventDate);
        return eventRepository.findByEventDate(date);
    }

    // Get events created after a specific date
    @GetMapping("/createdAfter/{date}")
    public List<Event> getEventsCreatedAfter(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        logger.info("Fetching events created after {}", localDate);
        return eventRepository.findByCreatedDateAfter(localDate);
    }

    // Get events with participants greater than a given number
    @GetMapping("/participants/{count}")
    public List<Event> getEventsByParticipants(@PathVariable Integer count) {
        logger.info("Fetching events with participants greater than {}", count);
        return eventRepository.findByExpectedParticipantsGreaterThan(count);
    }

    // Get events by status and date
    @GetMapping("/status/{status}/date/{eventDate}")
    public List<Event> getEventsByStatusAndDate(@PathVariable String status, @PathVariable String eventDate) {
        LocalDate date = LocalDate.parse(eventDate);
        logger.info("Fetching events with status '{}' on date {}", status, date);
        return eventRepository.findByStatusAndEventDate(status, date);
    }
}