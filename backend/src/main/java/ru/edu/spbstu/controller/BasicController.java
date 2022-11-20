package ru.edu.spbstu.controller;

import java.util.List;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.edu.spbstu.model.User;
import ru.edu.spbstu.service.UserService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/basic-controller")
public class BasicController {

    private final UserService userService;

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
}
