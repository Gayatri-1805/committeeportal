package com.example.committeeportal.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.committeeportal.Entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{

}
