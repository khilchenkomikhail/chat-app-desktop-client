package ru.edu.spbstu.model.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatRole;
import ru.edu.spbstu.model.Message;
import ru.edu.spbstu.model.User;
import ru.edu.spbstu.model.UserChatDetails;
import ru.edu.spbstu.model.jpa.ChatJpa;
import ru.edu.spbstu.model.jpa.MessageJpa;
import ru.edu.spbstu.model.jpa.UserChatDetailsJpa;
import ru.edu.spbstu.model.jpa.UserJpa;

import java.util.Date;

class JpaToModelConverterTest {

    private final JpaToModelConverter jpaToModelConverter = new JpaToModelConverter();

    @Test
    void convertUserJpaToUser() {
        UserJpa userJpa = new UserJpa(
                1L,
                "login",
                "password",
                "email@mail.org",
                "image"
        );

        User expectedUser = new User(
                1L,
                "login",
                "password",
                "email@mail.org",
                "image"
        );

        User actualUser = jpaToModelConverter.convertUserJpaToUser(userJpa);
        Assertions.assertEquals(expectedUser, actualUser);
    }

    @Test
    void convertChatJpaToChat() {
        ChatJpa chatJpa = new ChatJpa(
                1L,
               "chatName"
        );

        Chat expectedChat = new Chat(
                1L,
                "chatName"
        );
        Chat actualChat = jpaToModelConverter.convertChatJpaToChat(chatJpa);
        Assertions.assertEquals(expectedChat, actualChat);
    }

    @Test
    void convertMessageJpaToMessage() {
        UserJpa userJpa = new UserJpa(
                1L,
                "login",
                "password",
                "email@mail.org",
                "image"
        );

        ChatJpa chatJpa = new ChatJpa(
                1L,
                "chatName"
        );

        Date date = new Date();

        MessageJpa messageJpa = new MessageJpa(
                1L,
                date,
                "message",
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                userJpa,
                userJpa,
                chatJpa
        );

        Message expectedMessage = new Message(
                1L,
                1L,
                1L,
                1L,
                date,
                "message"
        );
        Message actualMessage = jpaToModelConverter.convertMessageJpaToMessage(messageJpa);
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void convertUserChatDetailsJpaToUserChatDetails() {
        UserJpa userJpa = new UserJpa(
                1L,
                "login",
                "password",
                "email@mail.org",
                "image"
        );

        ChatJpa chatJpa = new ChatJpa(
                1L,
                "chatName"
        );

        UserChatDetailsJpa userChatDetailsJpa = new UserChatDetailsJpa(
                1L,
                userJpa,
                chatJpa,
                ChatRole.USER
        );
        UserChatDetails expectedUserChatDetails = new UserChatDetails(
                1L,
                1L,
                1L,
                ChatRole.USER
        );
        UserChatDetails actualUserChatDetails
                = jpaToModelConverter.convertUserChatDetailsJpaToUserChatDetails(userChatDetailsJpa);
        Assertions.assertEquals(expectedUserChatDetails, actualUserChatDetails);
    }
}