package br.com.ifmg.event_manager.repositories;

import br.com.ifmg.event_manager.entities.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecover, Long> {

    @Query("SELECT obj FROM PasswordRecover obj WHERE (obj.email = :token) AND (obj.expiration > :now)")
    public List<PasswordRecover> searchValidToken(String token, Instant now);

}
