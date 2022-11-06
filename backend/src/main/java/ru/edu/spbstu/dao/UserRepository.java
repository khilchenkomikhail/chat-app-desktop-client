package ru.edu.spbstu.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.edu.spbstu.model.jpa.UserJpa;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserJpa, Long> {

    Optional<UserJpa> getByLogin(String login);
}
