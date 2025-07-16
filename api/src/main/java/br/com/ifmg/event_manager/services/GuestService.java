package br.com.ifmg.event_manager.services;

import br.com.ifmg.event_manager.dtos.EmailDTO;
import br.com.ifmg.event_manager.dtos.GuestDTO;
import br.com.ifmg.event_manager.entities.Guest;
import br.com.ifmg.event_manager.entities.TableEntity;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GuestService {

    @Autowired
    GuestRepository guestRepository;

    @Autowired
    TableEntityRepository tableEntityRepository;

    @Autowired
    EmailService emailService;

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
    public List<GuestDTO> findAllByTableId(Long tableId) {
        if (!tableEntityRepository.existsById(tableId)) {
            throw new ResourceNotFound("Table not found: " + tableId);
        }
        List<Guest> guests = guestRepository.findAllByTableId(tableId);
        return guests.stream().map(GuestDTO::new).collect(Collectors.toList());
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

    public void sendConfirmationEmail(Long guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFound("Guest not found: " + guestId));

        if (guest.getEmail() == null || guest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Guest email is not provided");
        }

        TableEntity table = guest.getTable();
        if (table == null) {
            throw new IllegalArgumentException("Guest is not assigned to any table");
        }

        // Preparar o DTO de email
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(guest.getEmail());
        emailDTO.setSubject("Confirmação de Presença - Evento");

        String bodyTemplate = """
            Olá %s,
            
            Você foi convidado para o evento e foi alocado na Mesa #%d.
            
            %s
            
            Para confirmar sua presença, por favor acesse o link:
            http://seusite.com/confirm/%d
            
            Agradecemos sua atenção.
            
            Atenciosamente,
            Equipe do Evento
            """;

        String body = String.format(bodyTemplate,
                guest.getName(),
                table.getId(),
                table.getDescription() != null ? "Descrição: " + table.getDescription() : "",
                guest.getId());

        emailDTO.setBody(body);

        // Enviar o email
        emailService.sendMail(emailDTO);
    }


    private void copyDtoToEntity(GuestDTO dto, Guest entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setUrlPhoto(dto.getUrlPhoto() != null ? dto.getUrlPhoto().trim() : null );
        entity.setConfirmed(dto.getConfirmed());

        if (dto.getTableId() != null) {
            entity.setTable(tableEntityRepository.getReferenceById(dto.getTableId()));
        }
    }
}