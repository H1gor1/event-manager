package br.com.ifmg.event_manager.controllers;

import br.com.ifmg.event_manager.dtos.AddressDTO;
import br.com.ifmg.event_manager.services.AddressService;
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
@RequestMapping("/addresses")
@Tag(name = "Address", description = "Controller for managing addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping(produces = "application/json")
    @Operation(
            description = "List all addresses",
            summary = "List all addresses",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    public ResponseEntity<Page<AddressDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        Page<AddressDTO> addresses = addressService.findAll(pageable);

        return ResponseEntity.ok().body(addresses);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Find address by ID",
            summary = "Find address by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    public ResponseEntity<AddressDTO> findById(@PathVariable Long id){

        AddressDTO address = addressService.findById(id);
        return ResponseEntity.ok().body(address);
    }

    @PostMapping(produces = "application/json")
    @Operation(
            description = "Create a new address",
            summary = "Create a new address",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Address created"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<AddressDTO> insert(@RequestBody AddressDTO dto){

        dto = addressService.insert(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();

        return ResponseEntity.created(uri).body(dto);

    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @Operation(
            description = "Update an existing address",
            summary = "Update DDRESS information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<AddressDTO> update(@PathVariable Long id, @RequestBody AddressDTO dto){

        dto = addressService.update(id, dto);
        return ResponseEntity.ok().body(dto);

    }

    @DeleteMapping(value = "/{id}")
    @Operation(
            description = "Delete a ADDRESS by ID",
            summary = "Delete ADDRESS",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content - Address successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            }
    )
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
