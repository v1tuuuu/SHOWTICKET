package com.showticketing.pdv.service;

import com.showticketing.pdv.entity.Event;
import com.showticketing.pdv.enums.EventStatus;
import com.showticketing.pdv.exception.BusinessException;
import com.showticketing.pdv.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<Event> listActiveEvents() {
        return eventRepository.findByStatus(EventStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Event getEventByBusinessId(String eventId) {
        return eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new BusinessException("Evento não encontrado: " + eventId));
    }

    @Transactional
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
}
