package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User getUserById(int id);

    User addUser(User user);

    User updateUser(User user);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);
}
