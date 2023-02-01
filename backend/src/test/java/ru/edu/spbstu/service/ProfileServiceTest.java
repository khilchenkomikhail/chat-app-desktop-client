package ru.edu.spbstu.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.exception.ResourceNotFound;
import ru.edu.spbstu.model.converter.JpaToModelConverter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @InjectMocks
    private ProfileService profileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JpaToModelConverter converter;

    @Test
    void getUser_InvalidLogin() {
        String login = "login";
        given(userRepository.getByLogin(anyString())).willReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFound.class, () -> profileService.getUser(login));
        verify(userRepository).getByLogin(anyString());
    }

    @Test
    void getUser() {
    }

    @Test
    void updateEmail() {
    }

    @Test
    void updatePassword() {
    }

    @Test
    void getProfilePhoto() {
    }

    @Test
    void updateProfilePhoto() {
    }
}