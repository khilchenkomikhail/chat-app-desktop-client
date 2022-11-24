package ru.edu.spbstu.controller;

import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.edu.spbstu.controller.request.SignUpRequest;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.model.jpa.UserJpa;
import ru.edu.spbstu.service.LoginService;

@RestController
@AllArgsConstructor
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginService loginService;

    // TODO переделать эндпоинт для регистрации в соответствии с требованиями, вынести репозиторий и пароль в отдельный сервис
    @PostMapping("/sign-up")
    public void signUp(@RequestBody SignUpRequest request) {
        UserJpa user = new UserJpa();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setImage(request.getImage());

        userRepository.save(user);
    }

    @PatchMapping("/send-tmp-password")
    public void sendTmpPassword(@RequestBody String login) {
        loginService.sendTemporaryPassword(login);
    }
}
