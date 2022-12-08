package ru.edu.spbstu.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.exception.ResourceNotFound;
import ru.edu.spbstu.model.jpa.UserJpa;
import ru.edu.spbstu.request.UpdateProfilePhotoRequest;

@Service
@AllArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    public byte[] getProfilePhoto(String login) {
        UserJpa user = userRepository.getByLogin(login)
                .orElseThrow(() -> new ResourceNotFound("User with login '" + login + "' was not found"));
        return user.getImage();
    }

    public void updateProfilePhoto(UpdateProfilePhotoRequest request) {
        userRepository.getByLogin(request.getLogin())
                .orElseThrow(() -> new ResourceNotFound("User with login '" + request.getLogin() + "' was not found"));
        userRepository.updateProfileImage(request.getLogin(), request.getImage());
    }
}
