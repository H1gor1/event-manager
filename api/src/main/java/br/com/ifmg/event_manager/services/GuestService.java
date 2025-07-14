package br.com.ifmg.event_manager.services;

import br.com.ifmg.event_manager.dtos.GuestDTO;
import br.com.ifmg.event_manager.entities.Guest;
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
public class GuestService {

    @Autowired
    GuestRepository guestRepository;

    @Autowired
    TableEntityRepository tableEntityRepository;

    @Transactional(readOnly = true)
    public Page<GuestDTO> findAll(Pageable pageable) {
        Page<Guest> guests = guestRepository.findAll(pageable);
        return guests.map(GuestDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<GuestDTO> findByTableId(Long tableId, Pageable pageable) {
        if (!tableEntityRepository.existsById(tableId)) {
            throw new ResourceNotFound("Table not found: " + tableId);
        }
        Page<Guest> guests = guestRepository.findByTableId(tableId, pageable);
        return guests.map(GuestDTO::new);
    }

    @Transactional(readOnly = true)
    public GuestDTO findById(Long id) {
        Optional<Guest> obj = guestRepository.findById(id);
        Guest guest = obj.orElseThrow(() -> new ResourceNotFound("Guest not found: " + id));
        return new GuestDTO(guest);
    }

    @Transactional
    public GuestDTO insert(GuestDTO dto) {
        Guest entity = new Guest();
        copyDtoToEntity(dto, entity);
        entity = guestRepository.save(entity);
        return new GuestDTO(entity);
    }

    @Transactional
    public GuestDTO update(Long id, GuestDTO dto) {
        try {
            Guest entity = guestRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = guestRepository.save(entity);
            return new GuestDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Guest not found: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!guestRepository.existsById(id)) {
            throw new ResourceNotFound("Guest not found: " + id);
        }
        try {
            guestRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Guest not found: " + id);
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation: ");
        }
    }

    private void copyDtoToEntity(GuestDTO dto, Guest entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setConfirmed(dto.getConfirmed());

        if (dto.getTableId() != null) {
            entity.setTable(tableEntityRepository.getReferenceById(dto.getTableId()));
        }
    }
}