package ru.edu.spbstu.security.datemanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenCreationDateRepo extends JpaRepository<TokenCreationDate, Long> {
    TokenCreationDate getByLogin(String login);
    void deleteByLogin(String login);
}
