package br.com.ifmg.event_manager.repositories;

import br.com.ifmg.event_manager.entities.Guest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Page<Guest> findByTableId(Long tableId, Pageable pageable);
}