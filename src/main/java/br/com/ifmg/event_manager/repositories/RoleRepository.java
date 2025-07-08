package br.com.ifmg.event_manager.repositories;

import br.com.ifmg.event_manager.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findByAuthority(String authority);
}
