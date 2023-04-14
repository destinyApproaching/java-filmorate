package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final List<User> users = new ArrayList<>();
    private int id = 1;

    @Override
    public List<User> getUsers() {
        return users;
    }

    public User getUserById(int id) {
        Optional<User> user = users.stream()
                .filter(x -> x.getId() == id)
                .findFirst();
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException(String.format("Пользователя с id=%d не существует.", id));
    }

    @Override
    public User addUser(User user) {
        isValid(user);
        if (user.getId() == null) {
            user.setId(id);
            increaseId();
        }
        users.add(user);
        log.info(String.format("Пользователь %s успешно добавлен.", user.getLogin()));
        return user;
    }

    @Override
    public User updateUser(User user) {
        isValid(user);
        for (User findUser : users) {
            if (Objects.equals(user.getId(), findUser.getId())) {
                int index = users.indexOf(findUser);
                users.set(index, user);
                log.info(String.format("Пользователь %s успешно обновлён.", user.getLogin()));
                return user;
            }
        }
        throw new UserNotFoundException(String.format("Пользователя с id=%d нет.", user.getId()));
    }

    @Override
    public void addFriend(int id, int friendId) {
        getUserById(id).addFriend(getUserById(friendId));
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        getUserById(id).deleteFriend(getUserById(friendId));
    }

    private void increaseId() {
        id++;
    }

    private void isValid(User user) {
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.debug("Пользователь не указал имя, вместо имени будет использоваться логин");
        }
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта указана неверно");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
