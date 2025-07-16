package br.com.ifmg.event_manager.services;

import br.com.ifmg.event_manager.dtos.*;
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

import java.time.format.DateTimeFormatter;
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

    @Transactional
    public TicketDTO confirmGuestAttendance(Long guestId, Long eventId, Long tableId) {
        // Verificar se o convidado existe
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFound("Guest not found: " + guestId));

        // Verificar se a mesa existe e se o convidado pertence a ela
        TableEntity table = tableEntityRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFound("Table not found: " + tableId));

        if (!guest.getTable().getId().equals(tableId)) {
            throw new ResourceNotFound("Guest does not belong to the specified table");
        }

        // Verificar se a mesa pertence ao evento informado
        if (!table.getEvent().getId().equals(eventId)) {
            throw new ResourceNotFound("Table does not belong to the specified event");
        }

        // Atualizar o status de confirmação do convidado
        guest.setConfirmed(true);
        guestRepository.save(guest);

        // Criar o DTO de resposta
        EventDTO eventDTO = new EventDTO(table.getEvent());
        AddressDTO addressDTO = table.getEvent().getAddress() != null ?
                new AddressDTO(table.getEvent().getAddress()) : null;
        TableEntityDTO tableDTO = new TableEntityDTO(table);

        return new TicketDTO(eventDTO, addressDTO, tableDTO, guest.getName(), guest.getUrlPhoto(), true);
    }

    @Transactional(readOnly = true)
    public TicketDTO getGuestTicketDetails(Long guestId) {
        // Verificar se o convidado existe
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFound("Guest not found: " + guestId));

        // Verificar se o convidado tem uma mesa atribuída
        if (guest.getTable() == null) {
            throw new ResourceNotFound("Guest does not have a table assigned");
        }

        TableEntity table = guest.getTable();

        // Criar o DTO de resposta
        EventDTO eventDTO = new EventDTO(table.getEvent());
        AddressDTO addressDTO = table.getEvent().getAddress() != null ?
                new AddressDTO(table.getEvent().getAddress()) : null;
        TableEntityDTO tableDTO = new TableEntityDTO(table);

        return new TicketDTO(eventDTO, addressDTO, tableDTO, guest.getName(), guest.getUrlPhoto(), guest.getConfirmed());
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

    @Transactional
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

        Long eventId = table.getEvent().getId();
        Long tableId = table.getId();

        // Criar um token de confirmação com os IDs necessários
        String confirmationLink = String.format(
                "http://localhost:4200/events/public/confirm-guest?guestId=%d&eventId=%d&tableId=%d",
                guestId, eventId, tableId);

        // Preparar o DTO de email
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(guest.getEmail());
        emailDTO.setSubject("Confirmação de Presença - " + table.getEvent().getName());

        String bodyTemplate = """
        Olá %s,
        
        Você foi convidado para o evento "%s" e foi alocado na Mesa #%d.
        
        %s
        
        Data do evento: %s
        Local: %s
        
        Para confirmar sua presença, por favor clique no link abaixo:
        %s
        
        Após a confirmação, você receberá um ticket digital que poderá ser apresentado no dia do evento.
        
        Agradecemos sua atenção.
        
        Atenciosamente,
        Equipe do Evento
        """;

        // Formatando o endereço para o email
        String addressFormatted = "";
        if (table.getEvent().getAddress() != null) {
            var address = table.getEvent().getAddress();
            addressFormatted = String.format("%s, %d - %s, %s/%s",
                    address.getStreet(),
                    address.getNumber(),
                    address.getNeighborhood(),
                    address.getCity(),
                    address.getState());
        }

        String body = String.format(bodyTemplate,
                guest.getName(),
                table.getEvent().getName(),
                table.getId(),
                table.getDescription() != null ? "Descrição da mesa: " + table.getDescription() : "",
                table.getEvent().getEventDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                addressFormatted,
                confirmationLink);

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