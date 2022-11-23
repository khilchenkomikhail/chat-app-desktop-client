package ru.edu.spbstu.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ChatUser {
    private String login;
    private Boolean is_admin;
}
