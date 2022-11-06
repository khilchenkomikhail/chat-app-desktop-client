package ru.edu.spbstu.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {
    private Long id;
    private Long sender_id;
    private Long author_id;
    private Long chat_id;
    private Date date;
    private String content;
}
