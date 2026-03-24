package edu.esi.ds.esiusuarios.repository;

import edu.esi.ds.esiusuarios.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetTokenDAO extends JpaRepository<ResetToken, String> {
    ResetToken findByToken(String token);
}