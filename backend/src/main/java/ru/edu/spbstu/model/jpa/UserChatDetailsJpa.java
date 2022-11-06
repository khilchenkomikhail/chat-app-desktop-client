package ru.edu.spbstu.model.jpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.edu.spbstu.model.ChatRole;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_chat_details")
public class UserChatDetailsJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserJpa user;

    @OneToOne
    @JoinColumn(name = "chat_id")
    private ChatJpa chat;

    @Enumerated(EnumType.STRING)
    private ChatRole chatRole;
}
