package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User getUser(int id);

    List<User> getUserFriends(int id);

    List<User> getMutualFriends(int id, int friendId);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);
}
