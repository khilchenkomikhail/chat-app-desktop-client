package ru.edu.spbstu.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.edu.spbstu.model.jpa.UserChatDetailsJpa;

@Repository
public interface UserChatDeviceRepository extends CrudRepository<UserChatDetailsJpa, Long> {
}
