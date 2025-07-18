package br.com.ifmg.event_manager.repositories;

import br.com.ifmg.event_manager.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByUserId(Long userId, Pageable pageable);

    Page<Event> findByUserEmail(String userEmail, Pageable pageable);

}
