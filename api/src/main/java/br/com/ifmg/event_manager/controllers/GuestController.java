package br.com.ifmg.event_manager.controllers;

import br.com.ifmg.event_manager.dtos.GuestDTO;
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

    @GetMapping(value = "/by-table/{tableId}", produces = "application/json")
    @Operation(
            description = "List all guests for a specific table",
            summary = "List guests by table ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Table Not Found")
            }
    )
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

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Find guest by ID",
            summary = "Find guest by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    public ResponseEntity<GuestDTO> findById(@PathVariable Long id){

        GuestDTO guest = guestService.findById(id);
        return ResponseEntity.ok().body(guest);
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        guestService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
