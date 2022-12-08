package ru.edu.spbstu.controller;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.edu.spbstu.request.CheckEmailRequest;
import ru.edu.spbstu.request.SignUpRequest;
import ru.edu.spbstu.service.LoginService;

@RestController
@AllArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/register")
    public void signUp(@RequestBody SignUpRequest request) {
        loginService.signUp(request);
    }

    @PostMapping("/check_user_email")
    public void checkUserEmail(@RequestBody CheckEmailRequest request) {
        loginService.checkUserEmail(request);
    }

    @PatchMapping("/send-tmp-password")
    public void sendTmpPassword(@RequestBody String login) {
        loginService.sendTemporaryPassword(login);
    }

    @GetMapping("/is_user_present")
    public Boolean isUserPresent(@RequestParam String login) {
        return loginService.isUserPresent(login);
    }
}
