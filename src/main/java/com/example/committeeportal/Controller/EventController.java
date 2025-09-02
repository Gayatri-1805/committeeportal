package com.example.committeeportal.Controller;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
//------Basic CRUD operations for Event entity------//
    //Get all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    //Get event by id
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventRepository.findById(id).orElse(null);
    }
    
    //Create new event
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }
    
    //update full event
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event updatedEvent) {
        return eventRepository.findById(id).map(event -> {
            event.setEventName(updatedEvent.getEventName());
            event.setEventDate(updatedEvent.getEventDate());
            event.setExpectedParticipants(updatedEvent.getExpectedParticipants());
            event.setCreatedDate(updatedEvent.getCreatedDate());
            event.setStatus(updatedEvent.getStatus());
            event.setCommittee(updatedEvent.getCommittee());
            return eventRepository.save(event);
        }).orElse(null);
    }
    
    //update partial event
    @PatchMapping("/{id}")
    public Event partiallyUpdateEvent(@PathVariable Long id, @RequestBody Event partialEvent) {
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isPresent()) {
            Event event = existingEvent.get();
            if (partialEvent.getEventName() != null) event.setEventName(partialEvent.getEventName());
            if (partialEvent.getEventDate() != null) event.setEventDate(partialEvent.getEventDate());
            if (partialEvent.getExpectedParticipants() != null) event.setExpectedParticipants(partialEvent.getExpectedParticipants());
            if (partialEvent.getCreatedDate() != null) event.setCreatedDate(partialEvent.getCreatedDate());
            if (partialEvent.getStatus() != null) event.setStatus(partialEvent.getStatus());
            if (partialEvent.getCommittee() != null) event.setCommittee(partialEvent.getCommittee());
            return eventRepository.save(event);
        }
        return null;
    }    
    
    //Delete event by id
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {
        if(eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return "Event deleted successfully!";
        } else {
            return "Event not found!";
        }
    }

// -----------------Custom Queries-----------------//
   // Get events by status
    @GetMapping("/status/{status}")
    public List<Event> getEventsByStatus(@PathVariable String status) {
        return eventRepository.findByStatus(status);
    }

    // Get events by committeeId
    @GetMapping("/committee/{committeeId}")
    public List<Event> getEventsByCommittee(@PathVariable Long committeeId) {
        return eventRepository.findByCommittee_Id(committeeId);
    }

    // Get events by date (format: yyyy-MM-dd)
    @GetMapping("/date/{eventDate}")
    public List<Event> getEventsByDate(@PathVariable String eventDate) {
        LocalDate date = LocalDate.parse(eventDate);
        return eventRepository.findByEventDate(date);
    }

    // Get events created after a specific date
    @GetMapping("/createdAfter/{date}")
    public List<Event> getEventsCreatedAfter(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return eventRepository.findByCreatedDateAfter(localDate);
    }

    // Get events with participants greater than a given number
    @GetMapping("/participants/{count}")
    public List<Event> getEventsByParticipants(@PathVariable Integer count) {
        return eventRepository.findByExpectedParticipantsGreaterThan(count);
    }

    // Get events by status and date
    @GetMapping("/status/{status}/date/{eventDate}")
    public List<Event> getEventsByStatusAndDate(@PathVariable String status, @PathVariable String eventDate) {
        LocalDate date = LocalDate.parse(eventDate);
        return eventRepository.findByStatusAndEventDate(status, date);
    }
}









