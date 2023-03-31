package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.expection.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final List<User> users = new ArrayList<>();
    private int id = 1;

    @GetMapping
    public List<User> getUsers() {
        return users;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        isValid(user);
        if (user.getId() == null) {
            user.setId(id);
            increaseId();
        }
        users.add(user);
        logDebug("Пользователь успешно добавлен");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        isValid(user);
        for (User findUser : users) {
            if (Objects.equals(user.getId(), findUser.getId())) {
                int index = users.indexOf(findUser);
                users.set(index, user);
                logDebug("Пользователь успешно обновлён");
                return user;
            }
        }
        throw new ValidationException("Пользователя с таким id нет");
    }

    private void increaseId() {
        id++;
    }

    private void logDebug(String string) {
        log.debug(string);
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
