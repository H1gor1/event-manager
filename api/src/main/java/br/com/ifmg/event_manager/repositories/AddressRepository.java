package br.com.ifmg.event_manager.repositories;

import br.com.ifmg.event_manager.entities.Address;
import br.com.ifmg.event_manager.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findByEvent(Optional<Event> event);
}
