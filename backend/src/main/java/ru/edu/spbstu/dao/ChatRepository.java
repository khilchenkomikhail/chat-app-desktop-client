package ru.edu.spbstu.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.edu.spbstu.model.jpa.ChatJpa;

@Repository
public interface ChatRepository extends CrudRepository<ChatJpa, Long> {
}
