package br.com.ifmg.event_manager.services;

import br.com.ifmg.event_manager.dtos.EventDTO;
import br.com.ifmg.event_manager.entities.Event;
import br.com.ifmg.event_manager.entities.User;
import br.com.ifmg.event_manager.repositories.EventRepository;
import br.com.ifmg.event_manager.repositories.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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

    @Transactional(readOnly = true)
    public Page<EventDTO> findEventsByUserEmail(String userEmail, Pageable pageable) {
         User user = userRepository.findByEmail(userEmail);
         if (user == null) {
             throw new ResourceNotFound("User not found with email: " + userEmail);
         }
         Page<Event> events = eventRepository.findByUserId(user.getId(), pageable);

        return events.map(EventDTO::new);
    }


    @Transactional
    public EventDTO insert(EventDTO dto) {
        Event entity = new Event();
        copyToEntity(dto, entity);
        entity = eventRepository.save(entity);
        return new EventDTO(entity);
    }

    @Transactional
    public EventDTO insertWithUserEmail(EventDTO dto, String userEmail) {
        // Buscar o usuário pelo email
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFound("User not found with email: " + userEmail);
        }

        // Configurar o ID do usuário no DTO
        dto.setUserId(user.getId());

        // Usar o insert existente
        return insert(dto);
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

        // Associar o usuário ao evento, se userId estiver presente
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFound("User not found: " + dto.getUserId()));
            entity.setUser(user);
        }
    }
}