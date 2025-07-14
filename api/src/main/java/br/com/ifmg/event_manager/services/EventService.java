package br.com.ifmg.event_manager.services;

import br.com.ifmg.event_manager.dtos.EventDTO;
import br.com.ifmg.event_manager.entities.Event;
import br.com.ifmg.event_manager.repositories.EventRepository;
import br.com.ifmg.event_manager.services.exceptions.DatabaseException;
import br.com.ifmg.event_manager.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Transactional(readOnly = true)
    public Page<EventDTO> findAll(Pageable pegeable) {
        Page<Event> events = eventRepository.findAll(pegeable);
        return events.map(EventDTO::new);
    }

    @Transactional(readOnly = true)
    public EventDTO findById(Long id) {
        Optional<Event> obj = eventRepository.findById(id);
        Event event = obj.orElseThrow(() -> new ResourceNotFound("Event not found: " + id));
        return new EventDTO(event);
    }

    @Transactional
    public EventDTO insert(EventDTO dto) {
        Event entity = new Event();
        copyToEntity(dto, entity);
        entity = eventRepository.save(entity);
        return new EventDTO(entity);
    }

    @Transactional
    public EventDTO update(Long id, EventDTO dto) {
        try {
            Event entity = eventRepository.getReferenceById(id);
            copyToEntity(dto, entity);
            entity = eventRepository.save(entity);
            return new EventDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Event not found: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFound("Event not found: " + id);
        }
        try {
            eventRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Event not found: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation: ");
        }
    }

    private void copyToEntity(EventDTO dto, Event entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setEventDate(dto.getEventDate());
        entity.setGuestNumber(dto.getGuestNumber());
    }
}
