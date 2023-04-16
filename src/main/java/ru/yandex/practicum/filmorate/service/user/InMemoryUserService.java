package ru.yandex.practicum.filmorate.service.user;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class InMemoryUserService implements UserService {
    @Getter
    private final UserStorage userStorage;

    @Autowired
    public InMemoryUserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
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
    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @Override
    public void addFriend(int id, int friendId) {
        userStorage.addFriend(id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        userStorage.deleteFriend(id, friendId);
    }
}