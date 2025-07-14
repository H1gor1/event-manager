package br.com.ifmg.event_manager.services;

import br.com.ifmg.event_manager.dtos.AddressDTO;
import br.com.ifmg.event_manager.entities.Address;
import br.com.ifmg.event_manager.repositories.AddressRepository;
import br.com.ifmg.event_manager.services.exceptions.DatabaseException;
import br.com.ifmg.event_manager.services.exceptions.ResourceNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public Page<AddressDTO> findAll(Pageable pageable) {
        Page<Address> addresses = addressRepository.findAll(pageable);
        return addresses.map(AddressDTO::new);
    }

    @Transactional(readOnly = true)
    public AddressDTO findById(Long id) {
        Optional<Address> obj = addressRepository.findById(id);
        Address address = obj.orElseThrow(() -> new ResourceNotFound("Address not found: " + id));
        return new AddressDTO(address);
    }

    @Transactional
    public AddressDTO insert(AddressDTO dto) {
        Address entity = new Address();
        copyToEntity(dto, entity);
        entity = addressRepository.save(entity);
        return new AddressDTO(entity);
    }

    @Transactional
    public AddressDTO update(Long id, AddressDTO dto) {
        try {
            Address entity = addressRepository.getReferenceById(id);
            copyToEntity(dto, entity);
            entity = addressRepository.save(entity);
            return new AddressDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound("Address not found: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if(!addressRepository.existsById(id)) {
            throw new ResourceNotFound("Address not found: " + id);
        }
        try{
            addressRepository.deleteById(id);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFound("Address not found: " + id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyToEntity(AddressDTO dto, Address entity) {
        entity.setStreet(dto.getStreet());
        entity.setNumber(dto.getNumber());
        entity.setComplement(dto.getComplement());
        entity.setNeighborhood(dto.getNeighborhood());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setZipCode(dto.getZipCode());
    }

}
