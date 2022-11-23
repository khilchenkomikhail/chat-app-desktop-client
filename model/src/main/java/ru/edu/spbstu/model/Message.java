package ru.edu.spbstu.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {
    private Long id;
    private String sender_login;
    private String author_login;
    private Long chat_id;
    private Date date;
    private String content;
    private Boolean is_deleted;
    private Boolean is_edited;
    private Boolean is_forwarded;
}
