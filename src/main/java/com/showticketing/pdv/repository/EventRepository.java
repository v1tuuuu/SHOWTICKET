package com.showticketing.pdv.repository;

import com.showticketing.pdv.entity.Event;
import com.showticketing.pdv.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventId(String eventId);
    List<Event> findByStatus(EventStatus status);
}
