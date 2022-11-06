package ru.edu.spbstu.model.jpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_devices")
public class UserDeviceJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deviceIp;
    private Date lastSignIn;
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserJpa user;
}

