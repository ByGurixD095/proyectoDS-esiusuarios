package edu.esi.ds.esiusuarios.repository;

import edu.esi.ds.esiusuarios.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User, String> {
    User findByName(String name);

    User findByEmail(String email);
}