package ru.yandex.practicum.filmorate.service.user;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InMemoryUserService implements UserService {
    @Getter
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public InMemoryUserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public User getUser(int id) {
        return inMemoryUserStorage.getUserById(id);
    }

    @Override
    public List<User> getUserFriends(int id) {
        List<User> usersFriends = new ArrayList<>();
        for (Integer friend : inMemoryUserStorage.getUserById(id).getFriends()) {
            usersFriends.add(inMemoryUserStorage.getUserById(friend));
        }
        return usersFriends;
    }

    @Override
    public List<User> getMutualFriends(int id, int friendId) {
        List<User> usersFriends = new ArrayList<>();
        Set<Integer> mutual = new HashSet<>(inMemoryUserStorage.getUserById(id).getFriends());
        mutual.retainAll(inMemoryUserStorage.getUserById(friendId).getFriends());
        for (Integer friend : mutual) {
            usersFriends.add(inMemoryUserStorage.getUserById(friend));
        }
        return usersFriends;
    }

    @Override
    public void addFriend(int id, int friendId) {
        inMemoryUserStorage.getUserById(id).addFriend(inMemoryUserStorage.getUserById(friendId));
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        inMemoryUserStorage.getUserById(id).deleteFriend(inMemoryUserStorage.getUserById(friendId));
    }
}