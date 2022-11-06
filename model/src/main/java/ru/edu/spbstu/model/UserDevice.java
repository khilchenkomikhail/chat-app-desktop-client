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
public class UserDevice {
    private Long id;
    private Long user_id;
    private String deviceIp;
    private Date lastSignIn;
    private String token;
}

