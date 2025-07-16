package br.com.ifmg.event_manager.repositories;

import br.com.ifmg.event_manager.entities.Guest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    Page<Guest> findByTableId(Long tableId, Pageable pageable);

    List<Guest> findAllByTableId(Long tableId);
}