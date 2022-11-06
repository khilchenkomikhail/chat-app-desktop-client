package ru.edu.spbstu.model;

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
public class UserChatDetails {
    private Long user_chat_id;
    private Long user_id;
    private Long chat_id;
    private ChatRole chatRole;
}
