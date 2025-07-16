package br.com.ifmg.event_manager.controllers;

import br.com.ifmg.event_manager.dtos.TableEntityDTO;
import br.com.ifmg.event_manager.services.TableEntityService;
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
@RequestMapping("/table-entities")
@Tag(name = "TableEntity", description = "Controller for managing table entities")
public class TableEntityController {

    @Autowired
    private TableEntityService tableEntityService;

    @GetMapping(produces = "application/json")
    @Operation(
            description = "List all tables",
            summary = "List all tables",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<TableEntityDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<TableEntityDTO> tables = tableEntityService.findAll(pageable);

        return ResponseEntity.ok().body(tables);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Find table by ID",
            summary = "Find table by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<TableEntityDTO> findById(@PathVariable Long id){

        TableEntityDTO table = tableEntityService.findById(id);
        return ResponseEntity.ok().body(table);
    }

    @GetMapping(value = "/by-event/{eventId}", produces = "application/json")
    @Operation(
            description = "List all tables for a specific event",
            summary = "List tables by event ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Event Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Page<TableEntityDTO>> findByEventId(
            @PathVariable Long eventId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<TableEntityDTO> tables = tableEntityService.findByEventId(eventId, pageable);
        return ResponseEntity.ok().body(tables);
    }

    @PostMapping(produces = "application/json")
    @Operation(
            description = "Create a new table",
            summary = "Create a new table",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Table created"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<TableEntityDTO> insert(@RequestBody TableEntityDTO dto){

        dto = tableEntityService.insert(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(dto);

    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Update an existing table",
            summary = "Update table information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<TableEntityDTO> update(@PathVariable Long id, @RequestBody TableEntityDTO dto){

        dto = tableEntityService.update(id, dto);
        return ResponseEntity.ok().body(dto);

    }

    @DeleteMapping(value = "/{id}")
    @Operation(
            description = "Delete a table by ID",
            summary = "Delete table",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content - Table successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        tableEntityService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
