package ru.edu.spbstu.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

public class Chat {
    private Long id;
    private String name;

    @Override
    public String toString() {
        if(this==new Chat())
        {
            return "";
        }
        return name;
    }
}
