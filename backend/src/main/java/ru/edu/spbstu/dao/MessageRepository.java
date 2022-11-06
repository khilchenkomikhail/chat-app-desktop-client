package ru.edu.spbstu.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.edu.spbstu.model.jpa.MessageJpa;

@Repository
public interface MessageRepository extends CrudRepository<MessageJpa, Long> {
}
