package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class UserTests extends FilmorateApplicationTests {
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
        userController = new UserController();
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

        assertEquals("Электронная почта указана неверно" , userController.addUser(user));
    }

    @Test
    public void shouldAddUserWhenEmailIsIncorrect() {
        log.debug("Тест на добавление пользователя с некорректный email");
        user.setEmail("yandex.ru");
        userController.addUser(user);
        assertEquals(0, userController.getUsers().size());
    }

    @Test
    public void shouldAddUserWhenLoginIsEmpty() {
        log.debug("Тест на добавление пользователя с пустым логином");
        user.setLogin("");
        userController.addUser(user);
        assertEquals(0, userController.getUsers().size());
    }

    @Test
    public void shouldAddUserWhenLoginWithBlank() {
        log.debug("Тест на добавление пользователя, логин которого содержит пробел");
        user.setLogin("Hamster  Attack");
        userController.addUser(user);
        assertEquals(0, userController.getUsers().size());
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
        userController.addUser(user);
        assertEquals(0, userController.getUsers().size());
    }
}
