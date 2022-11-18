package ru.edu.spbstu.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class ChatUpdateRequest {
    private Long chat_id;
    private List<String> user_logins;
}
