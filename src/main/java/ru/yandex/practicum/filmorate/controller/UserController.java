package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.InMemoryUserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final InMemoryUserService inMemoryUserService;

    @Autowired
    public UserController(InMemoryUserService inMemoryUserService) {
        this.inMemoryUserService = inMemoryUserService;
    }

    @GetMapping
    public List<User> getUsers() {
        return inMemoryUserService.getInMemoryUserStorage().getUsers();
    }

    @GetMapping("/{id}")
    public User getUsersById(@RequestBody @PathVariable int id) {
        return inMemoryUserService.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@RequestBody @PathVariable int id) {
        return inMemoryUserService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getUserFriends(@RequestBody @PathVariable int id, @PathVariable int otherId) {
        return inMemoryUserService.getMutualFriends(id, otherId);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return inMemoryUserService.getInMemoryUserStorage().addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return inMemoryUserService.getInMemoryUserStorage().updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable int id, @PathVariable int friendId) {
        inMemoryUserService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable int id, @PathVariable int friendId) {
        inMemoryUserService.deleteFriend(id, friendId);
    }
}