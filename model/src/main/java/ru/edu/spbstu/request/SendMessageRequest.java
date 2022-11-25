package ru.edu.spbstu.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class SendMessageRequest {
    private String sender_login;
    private String author_login;
    private Long chat_id;
    private String content;
}
