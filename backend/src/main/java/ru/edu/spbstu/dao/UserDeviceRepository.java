package ru.edu.spbstu.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.edu.spbstu.model.jpa.UserDeviceJpa;

@Repository
public interface UserDeviceRepository extends CrudRepository<UserDeviceJpa, Long> {
}
