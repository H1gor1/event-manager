package br.com.ifmg.event_manager.services;

import br.com.ifmg.event_manager.dtos.TableEntityDTO;
import br.com.ifmg.event_manager.entities.TableEntity;
import br.com.ifmg.event_manager.repositories.EventRepository;
import br.com.ifmg.event_manager.repositories.GuestRepository;
import br.com.ifmg.event_manager.repositories.TableEntityRepository;
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
public class TableEntityService {

    @Autowired
    private TableEntityRepository tableEntityRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional(readOnly = true)
    public Page<TableEntityDTO> findAll(Pageable pageable) {
        Page<TableEntity> tableEntities = tableEntityRepository.findAll(pageable);
        return tableEntities.map(TableEntityDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<TableEntityDTO> findByEventId(Long eventId, Pageable pageable) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFound("Event not found: " + eventId);
        }
        Page<TableEntity> tableEntities = tableEntityRepository.findByEventId(eventId, pageable);
        return tableEntities.map(TableEntityDTO::new);
    }

    @Transactional(readOnly = true)
    public TableEntityDTO findById(Long id) {
        Optional<TableEntity> obj = tableEntityRepository.findById(id);
        TableEntity tableEntity = obj.orElseThrow(() -> new ResourceNotFound("Table not found: " + id));
        return new TableEntityDTO(tableEntity);
    }

    @Transactional
    public TableEntityDTO insert(TableEntityDTO dto) {
        TableEntity entity = new TableEntity();
        copyToEntity(dto, entity);
        entity = tableEntityRepository.save(entity);
        return new TableEntityDTO(entity);
    }

    @Transactional
    public TableEntityDTO update(Long id, TableEntityDTO dto) {
        try {
            TableEntity entity = tableEntityRepository.getReferenceById(id);
            copyToEntity(dto, entity);
            entity = tableEntityRepository.save(entity);
            return new TableEntityDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Table not found: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!tableEntityRepository.existsById(id)) {
            throw new ResourceNotFound("Table not found: " + id);
        }
        try {
            tableEntityRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Table not found: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyToEntity(TableEntityDTO dto, TableEntity entity) {
        entity.setDescription(dto.getDescription());
        entity.setGuestNumber(dto.getGuestNumber());

        if (dto.getEventId() != null) {
            entity.setEvent(eventRepository.getReferenceById(dto.getEventId()));
        }
    }
}