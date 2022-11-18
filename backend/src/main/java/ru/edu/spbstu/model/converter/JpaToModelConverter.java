package ru.edu.spbstu.model.converter;

import org.springframework.stereotype.Service;

import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.Message;
import ru.edu.spbstu.model.User;
import ru.edu.spbstu.model.UserChatDetails;
import ru.edu.spbstu.model.jpa.ChatJpa;
import ru.edu.spbstu.model.jpa.MessageJpa;
import ru.edu.spbstu.model.jpa.UserChatDetailsJpa;
import ru.edu.spbstu.model.jpa.UserJpa;

@Service
public class JpaToModelConverter {
    public User convertUserJpaToUser(UserJpa userJpa) {
        User user = new User();
        user.setLogin(userJpa.getLogin());
        user.setEmail(userJpa.getEmail());
        user.setImage(userJpa.getImage());
        user.setPassword(userJpa.getPassword());
        return user;
    }

    public Chat convertChatJpaToChat(ChatJpa chatJpa) {
        Chat chat = new Chat();
        chat.setId(chatJpa.getId());
        chat.setName(chatJpa.getName());
        return chat;
    }

    public Message convertMessageJpaToMessage(MessageJpa messageJpa) {
        Message message = new Message();
        message.setId(message.getId());
        message.setDate(messageJpa.getDate());
        message.setContent(messageJpa.getContent());
        message.setChat_id(messageJpa.getChat().getId());
        message.setSender_id(messageJpa.getSender().getId());
        message.setAuthor_id(messageJpa.getAuthor().getId());
        return message;
    }

    public UserChatDetails convertUserChatDetailsJpaToUserChatDetails(UserChatDetailsJpa userChatDetailsJpa) {
        UserChatDetails userChatDetails = new UserChatDetails();
        userChatDetails.setUser_chat_id(userChatDetailsJpa.getId());
        userChatDetails.setChatRole(userChatDetailsJpa.getChatRole());
        userChatDetails.setChat_id(userChatDetailsJpa.getChat().getId());
        userChatDetails.setUser_id(userChatDetailsJpa.getUser().getId());
        return userChatDetails;
    }
}
