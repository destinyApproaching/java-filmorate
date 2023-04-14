package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.InMemoryUserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class UserTests {
    private UserController userController;

    private final User user = User.builder()
            .id(1)
            .email("hamster@yandex.ru")
            .login("hamster2000")
            .name("Валентин Слуцкий")
            .birthday(LocalDate.of(1987, 9, 13))
            .build();

    @BeforeEach
    public void beforeEach() {
        userController = new UserController(new InMemoryUserService(new InMemoryUserStorage()));
    }

    @Test
    public void shouldAddUserWhenUserIsValid() {
        log.debug("Тест на добавление валидного пользователя");
        userController.addUser(user);
        assertEquals(1, userController.getUsers().size());
    }

    @Test
    public void shouldUpdateUserWhenUserIsValid() {
        log.debug("Тест на обновление валидного пользователя");
        userController.addUser(user);
        user.setEmail("hamster2000@mail.ru");
        userController.updateUser(user);
        assertEquals(user.getName(), userController.getUsers().get(0).getName());
    }

    @Test
    public void shouldAddUserWhenEmailIsEmpty() {
        log.debug("Тест на добавление пользователя с путым email");
        user.setEmail("");
        ValidationException ex = assertThrows(
                ValidationException.class, () -> userController.addUser(user)
        );
        assertEquals("Электронная почта указана неверно", ex.getMessage());
    }

    @Test
    public void shouldAddUserWhenEmailIsIncorrect() {
        log.debug("Тест на добавление пользователя с некорректный email");
        user.setEmail("yandex.ru");
        ValidationException ex = assertThrows(
                ValidationException.class, () -> userController.addUser(user)
        );
        assertEquals("Электронная почта указана неверно", ex.getMessage());
    }

    @Test
    public void shouldAddUserWhenLoginIsEmpty() {
        log.debug("Тест на добавление пользователя с пустым логином");
        user.setLogin("");
        ValidationException ex = assertThrows(
                ValidationException.class, () -> userController.addUser(user)
        );
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    public void shouldAddUserWhenLoginWithBlank() {
        log.debug("Тест на добавление пользователя, логин которого содержит пробел");
        user.setLogin("Hamster  Attack");
        ValidationException ex = assertThrows(
                ValidationException.class, () -> userController.addUser(user)
        );
        assertEquals("Логин не может быть пустым и содержать пробелы", ex.getMessage());
    }

    @Test
    public void shouldAddUserWhenNameIsEmpty() {
        log.debug("Тест на добавление пользователя с пустым именем");
        user.setName("");
        userController.addUser(user);
        assertEquals(user.getLogin(), userController.getUsers().get(0).getName());
    }

    @Test
    public void shouldAddUserWhenBirthdayInFuture() {
        log.debug("Тест на добавление пользователя рождённым в будущем");
        user.setBirthday(LocalDate.of(2024, 12, 20));
        ValidationException ex = assertThrows(
                ValidationException.class, () -> userController.addUser(user)
        );
        assertEquals("Дата рождения не может быть в будущем", ex.getMessage());
    }
}