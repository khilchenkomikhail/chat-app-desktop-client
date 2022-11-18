package ru.edu.spbstu.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SignUpRequest {
    private String login;
    private String password;
    private String email;
    private String image;
}
