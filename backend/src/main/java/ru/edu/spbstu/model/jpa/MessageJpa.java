package ru.edu.spbstu.model.jpa;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class MessageJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private String content;
    private Boolean isDeleted;
    private Boolean isEdited;
    private Boolean isForwarded;

    @OneToOne
    @JoinColumn(name = "sender_id")
    private UserJpa sender;


    @OneToOne
    @JoinColumn(name = "author_id")
    private UserJpa author;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatJpa chat;
}
