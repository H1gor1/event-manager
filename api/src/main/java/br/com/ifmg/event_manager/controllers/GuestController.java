package br.com.ifmg.event_manager.controllers;

import br.com.ifmg.event_manager.dtos.GuestDTO;
import br.com.ifmg.event_manager.dtos.TicketDTO;
import br.com.ifmg.event_manager.services.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/guests")
@Tag(name = "Guest", description = "Controller for managing guests")
public class GuestController {

    @Autowired
    private GuestService guestService;

    @GetMapping(produces = "application/json")
    @Operation(
            description = "List all guests",
            summary = "List all guests",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Page<GuestDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<GuestDTO> guests = guestService.findAll(pageable);

        return ResponseEntity.ok().body(guests);
    }


    @GetMapping("/public/{id}/status")
    @Operation(
            description = "Check if a guest has confirmed attendance",
            summary = "Public endpoint to check guest confirmation status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Guest not found")
            }
    )
    public ResponseEntity<Map<String, Boolean>> getGuestStatus(@PathVariable Long id) {
        GuestDTO guest = guestService.findById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("confirmed", guest.getConfirmed());
        return ResponseEntity.ok().body(response);
    }

    // Rota pública para obter os detalhes de um convidado para o ticket
    @GetMapping("/public/{id}/details")
    @Operation(
            description = "Get guest details for ticket generation",
            summary = "Public endpoint to get guest details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Details retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Guest not found")
            }
    )
    public ResponseEntity<TicketDTO> getGuestDetails(@PathVariable Long id) {
        TicketDTO ticket = guestService.getGuestTicketDetails(id);
        return ResponseEntity.ok().body(ticket);
    }

    // Rota pública para confirmar a presença de um convidado
    @PostMapping("/public/{id}/confirm")
    @Operation(
            description = "Confirm guest attendance",
            summary = "Public endpoint to confirm guest attendance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attendance confirmed successfully"),
                    @ApiResponse(responseCode = "404", description = "Guest not found")
            }
    )
    public ResponseEntity<TicketDTO> confirmGuestAttendance(
            @PathVariable Long id,
            @RequestBody Map<String, Long> requestBody) {

        Long eventId = requestBody.get("eventId");
        Long tableId = requestBody.get("tableId");

        if (eventId == null || tableId == null) {
            throw new IllegalArgumentException("eventId and tableId are required");
        }

        TicketDTO ticket = guestService.confirmGuestAttendance(id, eventId, tableId);
        return ResponseEntity.ok().body(ticket);
    }




    @GetMapping(value = "/by-table/{tableId}", produces = "application/json")
    @Operation(
            description = "List all guests for a specific table",
            summary = "List guests by table ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Table Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Page<GuestDTO>> findByTableId(
            @PathVariable Long tableId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<GuestDTO> guests = guestService.findByTableId(tableId, pageable);
        return ResponseEntity.ok().body(guests);
    }

    @GetMapping(value = "/all-by-table/{tableId}", produces = "application/json")
    @Operation(
            description = "Get all guests for a specific table without pagination",
            summary = "Get all guests by table ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Table Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<GuestDTO>> findAllByTableId(@PathVariable Long tableId) {
        List<GuestDTO> guests = guestService.findAllByTableId(tableId);
        return ResponseEntity.ok().body(guests);
    }


    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Find guest by ID",
            summary = "Find guest by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<GuestDTO> findById(@PathVariable Long id){

        GuestDTO guest = guestService.findById(id);
        return ResponseEntity.ok().body(guest);
    }

    @PostMapping(value = "/{id}/send-confirmation", produces = "application/json")
    @Operation(
            description = "Send confirmation email to a guest",
            summary = "Send confirmation email",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content - Email sent successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid guest data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found - Guest not found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Void> sendConfirmationEmail(@PathVariable Long id) {
        guestService.sendConfirmationEmail(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping(produces = "application/json")
    @Operation(
            description = "Create a new guest",
            summary = "Create a new guest",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Guest created"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<GuestDTO> insert(@RequestBody GuestDTO dto){

        dto = guestService.insert(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(dto);

    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Update an existing guest",
            summary = "Update guest information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<GuestDTO> update(@PathVariable Long id, @RequestBody GuestDTO dto){

        dto = guestService.update(id, dto);
        return ResponseEntity.ok().body(dto);

    }

    @DeleteMapping(value = "/{id}")
    @Operation(
            description = "Delete a guest by ID",
            summary = "Delete guest",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content - Guest successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
