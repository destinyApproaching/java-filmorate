package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserDaoService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserDaoService(UserDbStorage userDbStorage) {
        this.userStorage = userDbStorage;
    }

    @Override
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    @Override
    public List<User> getUserFriends(int id) {
        return userStorage.getUserFriends(id);
    }

    @Override
    public List<User> getMutualFriends(int id, int friendId) {
        return userStorage.getMutualFriends(id, friendId);
    }

    @Override
    public void addFriend(int id, int friendId) {
        userStorage.addFriend(id, friendId);
    }

    @Override
    public User addUser(User user) {
        userStorage.addUser(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        userStorage.deleteFriend(id, friendId);
    }
}
