package ru.edu.spbstu.controller;

import java.util.List;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.edu.spbstu.model.Chat;
import ru.edu.spbstu.model.User;
import ru.edu.spbstu.model.UserChatDetails;
import ru.edu.spbstu.service.ChatService;
import ru.edu.spbstu.service.UserService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/basic-controller")
public class BasicController {

    private final UserService userService;
    private final ChatService chatService;

    @GetMapping("/get-user")
    public User getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/add-user")
    public void addUser(@RequestParam User user) {
        userService.addUser(user);
    }

    @GetMapping("/get-all-users")
    public List<User> getAllUsers() {
        return userService.getAllUser();
    }

    @GetMapping("/get-all-chats")
    public List<Chat> getAllChats() {
        return chatService.getAllChats();
    }

    @GetMapping("/get-all-chat-details")
    public List<UserChatDetails> getAllChatDetails() {
        return chatService.getAllChatDetails();
    }
}
