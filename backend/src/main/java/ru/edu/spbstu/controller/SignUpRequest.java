package ru.edu.spbstu.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequest {
    private final String login;
    private final String password;
    private final String email;
    private final String image;
}
