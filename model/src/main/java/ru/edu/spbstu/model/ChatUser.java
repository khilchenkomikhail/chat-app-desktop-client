package ru.edu.spbstu.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatUser {
    private String login;
    private Boolean isAdmin;
}
