package br.com.ifmg.event_manager.controllers;

import br.com.ifmg.event_manager.dtos.EventDTO;
import br.com.ifmg.event_manager.services.EventService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import br.com.ifmg.event_manager.repositories.UserRepository;

import java.net.URI;

@RestController
@RequestMapping("/events")
@Tag(name = "Event", description = "Controller for managing events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(produces = "application/json")
    @Operation(
            description = "List all events",
            summary = "List all events",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<EventDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<EventDTO> events = eventService.findAll(pageable);

        return ResponseEntity.ok().body(events);
    }

    @GetMapping(value = "/user", produces = "application/json")
    @Operation(
            description = "List all events for the authenticated user",
            summary = "List user events",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Page<EventDTO>> findMyEvents(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userEmail = jwt.getClaimAsString("username");


        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<EventDTO> events = eventService.findEventsByUserEmail(userEmail, pageable);

        return ResponseEntity.ok().body(events);
    }


    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Find event by ID",
            summary = "Find event by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<EventDTO> findById(@PathVariable Long id){

        EventDTO event = eventService.findById(id);
        return ResponseEntity.ok().body(event);
    }

    @PostMapping(produces = "application/json")
    @Operation(
            description = "Create a new event",
            summary = "Create a new event",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Event created"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<EventDTO> insert(@RequestBody EventDTO dto, @AuthenticationPrincipal Jwt jwt){
        // Extrair o email do claim "username" do token JWT
        String userEmail = jwt.getClaimAsString("username");

        dto = eventService.insertWithUserEmail(dto, userEmail);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Update an existing event",
            summary = "Update event information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<EventDTO> update(@PathVariable Long id, @RequestBody EventDTO dto){

        dto = eventService.update(id, dto);
        return ResponseEntity.ok().body(dto);

    }

    @DeleteMapping(value = "/{id}")
    @Operation(
            description = "Delete a event by ID",
            summary = "Delete event",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content - Event successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
