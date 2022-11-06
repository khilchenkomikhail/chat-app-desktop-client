package ru.edu.spbstu.service;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.model.User;
import ru.edu.spbstu.model.converter.JpaToModelConverter;
import ru.edu.spbstu.model.jpa.UserJpa;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final JpaToModelConverter converter;

    private final UserRepository userRepository;

    public User getUserById(Long id) {
        return converter.convertUserJpaToUser(userRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new));
    }

    public List<User> getAllUser() {
        List<UserJpa> all = Lists.newArrayList(userRepository.findAll());
        return all.stream()
                .map(converter::convertUserJpaToUser)
                .collect(Collectors.toList());
    }

    public void addUser(User user) {
        UserJpa userJpa = new UserJpa(
                user.getId(),
                user.getLogin(),
                user.getPassword(),
                user.getEmail(),
                user.getImage()
        );
        userRepository.save(userJpa);
    }
}
