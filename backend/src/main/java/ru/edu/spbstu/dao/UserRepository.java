package ru.edu.spbstu.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.edu.spbstu.model.jpa.UserJpa;

@Repository
public interface UserRepository extends CrudRepository<UserJpa, Long> {

    Optional<UserJpa> getByLogin(String login);

    Optional<UserJpa> getByEmail(String email);
}
