package br.com.ifmg.event_manager.repositories;

import br.com.ifmg.event_manager.entities.TableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableEntityRepository extends JpaRepository<TableEntity, Long> {
    Page<TableEntity> findByEventId(Long eventId, Pageable pageable);
}