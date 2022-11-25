package ru.edu.spbstu.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class EditMessageRequest {
    private Long message_id;
    private String new_content;
}
