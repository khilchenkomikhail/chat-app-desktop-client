package ru.edu.spbstu.service;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.edu.spbstu.controller.ChatUpdateRequest;
import ru.edu.spbstu.controller.CreateChatRequest;
import ru.edu.spbstu.dao.ChatRepository;
import ru.edu.spbstu.dao.UserChatDetailsRepository;
import ru.edu.spbstu.dao.UserRepository;
import ru.edu.spbstu.exception.InvalidRequestParameter;
import ru.edu.spbstu.exception.ResourceNotFound;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.ChatRole;
import ru.edu.spbstu.model.ChatUser;
import ru.edu.spbstu.model.UserChatDetails;
import ru.edu.spbstu.model.converter.JpaToModelConverter;
import ru.edu.spbstu.model.jpa.ChatJpa;
import ru.edu.spbstu.model.jpa.UserChatDetailsJpa;
import ru.edu.spbstu.model.jpa.UserJpa;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {

    private final JpaToModelConverter converter;
    private final ChatRepository chatRepository;
    private final UserChatDetailsRepository userChatDetailsRepository;
    private final UserRepository userRepository;

    public List<Chat> getAllChats() {
        List<ChatJpa> all = Lists.newArrayList(chatRepository.findAll());
        return all.stream()
                .map(converter::convertChatJpaToChat)
                .collect(Collectors.toList());
    }

    public List<UserChatDetails> getAllChatDetails() {
        List<UserChatDetailsJpa> all = Lists.newArrayList(userChatDetailsRepository.findAll());
        return all.stream()
                .map(converter::convertUserChatDetailsJpaToUserChatDetails)
                .collect(Collectors.toList());
    }

    public List<Chat> getChats(String login, Integer pageNumber) {
        Optional<UserJpa> userJpa = userRepository.getByLogin(login);
        if (userJpa.isEmpty()) {
            throw new ResourceNotFound("User with login '" + login + "' was not found");
        }
        List<ChatJpa> userChats = chatRepository.getChatsByUserId(userJpa.get().getId());
        return getChatListByPage(userChats, pageNumber);
    }

    public List<Chat> getChatsBySearch(String login, String begin, Integer pageNumber) {
        Optional<UserJpa> userJpa = userRepository.getByLogin(login);
        if (userJpa.isEmpty()) {
            throw new ResourceNotFound("User with login '" + login + "' was not found");
        }
        List<ChatJpa> userChats = chatRepository.getChatsBySearch(userJpa.get().getId(), begin);
        return getChatListByPage(userChats, pageNumber);
    }

    public List<ChatUser> getChatMembers(Long chatId) {
        if (chatRepository.getById(chatId).isEmpty()) {
            throw new ResourceNotFound("Chat with id '" + chatId + "' was not found");
        }
        List<UserChatDetailsJpa> userChatDetailsJpaList = userChatDetailsRepository.getChatMembers(chatId);
        return userChatDetailsJpaList.stream().map(converter::convertUserChatDetailsJpaToChatUser)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createChat(CreateChatRequest request){
        ChatJpa chatJpa = new ChatJpa();
        chatJpa.setName(request.getChat_name());
        ChatJpa savedChatJpa = chatRepository.save(chatJpa);
        for (String login: request.getUser_logins()) {
            UserChatDetailsJpa userChatDetailsJpa = new UserChatDetailsJpa();
            Optional<UserJpa> userJpa = userRepository.getByLogin(login);
            if (userJpa.isEmpty()) {
                throw new ResourceNotFound("User with login '" + login + "' was not found");
            }
            userChatDetailsJpa.setChat(savedChatJpa);
            userChatDetailsJpa.setUser(userJpa.get());
            userChatDetailsJpa.setChatRole(ChatRole.USER);
            userChatDetailsRepository.save(userChatDetailsJpa);
        }
        UserChatDetailsJpa userChatDetailsJpa = new UserChatDetailsJpa();
        Optional<UserJpa> userJpa = userRepository.getByLogin(request.getAdmin_login());
        if (userJpa.isEmpty()) {
            throw new ResourceNotFound("User with login '" + request.getAdmin_login() + "' was not found");
        }
        userChatDetailsJpa.setChat(savedChatJpa);
        userChatDetailsJpa.setUser(userJpa.get());
        userChatDetailsJpa.setChatRole(ChatRole.ADMIN);
        userChatDetailsRepository.save(userChatDetailsJpa);
    }

    @Transactional
    public void deleteUsersFromChat(ChatUpdateRequest request) {
        Optional<ChatJpa> chatJpa = chatRepository.getById(request.getChat_id());
        if (chatJpa.isEmpty()) {
            throw new ResourceNotFound("Chat with id '" + request.getChat_id() + "' was not found");
        }
        for (String login: request.getUser_logins()) {
            Optional<UserJpa> userJpa = userRepository.getByLogin(login);
            if (userJpa.isEmpty()) {
                throw new ResourceNotFound("User with login '" + login + "' was not found");
            }
            userChatDetailsRepository.deleteByChatAndUser(chatJpa.get(), userJpa.get());
        }
    }

    @Transactional
    public void addUsersToChat(ChatUpdateRequest request) {
        Optional<ChatJpa> chatJpa = chatRepository.getById(request.getChat_id());
        if (chatJpa.isEmpty()) {
            throw new ResourceNotFound("Chat with id '" + request.getChat_id() + "' was not found");
        }
        for (String login: request.getUser_logins()) {
            UserChatDetailsJpa userChatDetailsJpa = new UserChatDetailsJpa();
            Optional<UserJpa> userJpa = userRepository.getByLogin(login);
            if (userJpa.isEmpty()) {
                throw new ResourceNotFound("User with login '" + login + "' was not found");
            }
            userChatDetailsJpa.setChat(chatJpa.get());
            userChatDetailsJpa.setUser(userJpa.get());
            userChatDetailsJpa.setChatRole(ChatRole.USER);
            userChatDetailsRepository.save(userChatDetailsJpa);
        }
    }

    @Transactional
    public void makeUsersAdmins(ChatUpdateRequest request) {
        Optional<ChatJpa> chatJpa = chatRepository.getById(request.getChat_id());
        if (chatJpa.isEmpty()) {
            throw new ResourceNotFound("Chat with id '" + request.getChat_id() + "' was not found");
        }
        for (String login: request.getUser_logins()) {
            Optional<UserJpa> userJpa = userRepository.getByLogin(login);
            if (userJpa.isEmpty()) {
                throw new ResourceNotFound("User with login '" + login + "' was not found");
            }
            userChatDetailsRepository.makeUsersAdmins(chatJpa.get().getId(), userJpa.get().getId());
        }
    }

    private List<Chat> getChatListByPage(List<ChatJpa> chatJpaList, Integer pageNumber) {
        int pageCapacity = 50;
        if (chatJpaList.size() <= pageCapacity * (pageNumber - 1)) {
            throw new InvalidRequestParameter("There are no chats on page " + pageNumber);
        }
        else if (chatJpaList.size() < pageCapacity * pageNumber) {
            return chatJpaList.subList(pageCapacity * (pageNumber - 1), chatJpaList.size()).stream()
                    .map(converter::convertChatJpaToChat)
                    .collect(Collectors.toList());
        }
        return chatJpaList.subList(pageCapacity * (pageNumber - 1), pageCapacity * pageNumber).stream()
                .map(converter::convertChatJpaToChat)
                .collect(Collectors.toList());
    }
}
