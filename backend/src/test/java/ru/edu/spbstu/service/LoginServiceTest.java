package ru.edu.spbstu.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.exception.ResourceNotFound;
import ru.edu.spbstu.mail.EmailSender;
import ru.edu.spbstu.model.Language;

import ru.edu.spbstu.model.jpa.UserJpa;
import ru.edu.spbstu.request.CheckEmailRequest;
import ru.edu.spbstu.request.SignUpRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailSender emailSender;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void sendTemporaryPassword_InvalidLogin() {
        String login = "login";

        given(userRepository.getByLogin(anyString())).willReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> loginService.sendTemporaryPassword(login, Language.RUSSIAN));

        verify(userRepository, times(1)).getByLogin(anyString());
    }

    @Test
    void sendTemporaryPassword_Success() {
        String login = "login";
        String password = "password";
        String email = "mail@mail.com";

        UserJpa userJpa = new UserJpa();
        userJpa.setLogin(login);
        userJpa.setPassword(password);
        userJpa.setEmail(email);
        userJpa.setImage("image");
        userJpa.setId(1L);

        given(userRepository.getByLogin(anyString())).willReturn(Optional.of(userJpa));
        doNothing().when(emailSender).send(anyString(), anyString(), isA(Language.class));

        loginService.sendTemporaryPassword(login, Language.ENGLISH);

        verify(userRepository, times(1)).getByLogin(anyString());
        verify(userRepository, times(1)).save(isA(UserJpa.class));
        verify(emailSender, times(1)).send(anyString(), anyString(), isA(Language.class));
    }

    @Test
    void checkUserEmail_InvalidLogin() {
        String login = "login";
        String email = "mail@mail.com";
        CheckEmailRequest request = new CheckEmailRequest(login, email);

        given(userRepository.getByLogin(anyString())).willReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> loginService.checkUserEmail(request));

        verify(userRepository, times(1)).getByLogin(anyString());
    }

    @Test
    void checkUserEmail_ValidLoginSuccess() {
        String login = "login";
        String password = "password";
        String email = "mail@mail.com";
        CheckEmailRequest request = new CheckEmailRequest(login, email);

        UserJpa userJpa = new UserJpa();
        userJpa.setLogin(login);
        userJpa.setPassword(password);
        userJpa.setEmail(email);
        userJpa.setImage("image");
        userJpa.setId(1L);

        given(userRepository.getByLogin(anyString())).willReturn(Optional.of(userJpa));

        assertTrue(loginService.checkUserEmail(request));

        verify(userRepository, times(1)).getByLogin(anyString());
    }

    @Test
    void checkUserEmail_InvalidLoginSuccess() {
        String login = "login";
        String password = "password";
        String email = "mail@mail.com";
        CheckEmailRequest request = new CheckEmailRequest(login, email);

        String email2 = "mail@mail.org";
        UserJpa userJpa = new UserJpa();
        userJpa.setLogin(login);
        userJpa.setPassword(password);
        userJpa.setEmail(email2);
        userJpa.setImage("image");
        userJpa.setId(1L);

        given(userRepository.getByLogin(anyString())).willReturn(Optional.of(userJpa));

        assertFalse(loginService.checkUserEmail(request));

        verify(userRepository, times(1)).getByLogin(anyString());
    }

    @Test
    void signUp() {
        SignUpRequest request = new SignUpRequest(
                "login",
                "password",
                "mail@mail.com");

        loginService.signUp(request);

        verify(userRepository, times(1)).save(isA(UserJpa.class));
    }

    @Test
    void isUserPresent() {
        String login1 = "login1";
        String login2 ="login2";
        UserJpa userJpa = new UserJpa(
                1L,
                login1,
                "password",
                "mail@mail.com",
                "image");

        given(userRepository.getByLogin(login2)).willReturn(Optional.empty());
        given(userRepository.getByLogin(login1)).willReturn(Optional.of(userJpa));

        assertFalse(loginService.isUserPresent(login2));
        assertTrue(loginService.isUserPresent(login1));

        verify(userRepository, times(1)).getByLogin(login1);
        verify(userRepository, times(1)).getByLogin(login2);
    }

    @Test
    void isEmailUsed() {
        String email1 = "mail@mail.com";
        String email2 = "mail@mail.org";
        UserJpa userJpa = new UserJpa(
                1L,
                "login",
                "password",
                email2,
                "image");

        given(userRepository.getByEmail(email1)).willReturn(Optional.empty());
        given(userRepository.getByEmail(email2)).willReturn(Optional.of(userJpa));

        assertFalse(loginService.isEmailUsed(email1));
        assertTrue(loginService.isEmailUsed(email2));

        verify(userRepository, times(1)).getByEmail(email1);
        verify(userRepository, times(1)).getByEmail(email2);
    }
}