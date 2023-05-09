package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUserById(int id);

    List<User> getUserFriends(int id);

    List<User> getMutualFriends(int id, int friendId);

    void addFriend(int id, int friendId);

    User addUser(User user);

    User updateUser(User user);

    void deleteFriend(int id, int friendId);
}