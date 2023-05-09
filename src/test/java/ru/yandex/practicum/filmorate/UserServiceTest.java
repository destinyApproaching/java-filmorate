package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;

    User user = User.builder()
            .login("userLogin")
            .email("user@ya.ru")
            .birthday(LocalDate.now())
            .build();

    @Test
    public void testCreateUser() {
        User userTest = User.builder()
                .id(-1)
                .login("testCreateUserMUserWithNewId")
                .email("testate@ya.ru")
                .birthday(LocalDate.now())
                .build();
        User expectUser = userService.addUser(userTest);

        assertNotEquals(-1, expectUser.getId(), "Пользователь не записан в базу, не присвоен id.");
    }

    @Test
    public void testFindUserById() {
        User userTest = userService.addUser(user);
        User expectUser = userService.getUserById(userTest.getId());
        assertEquals(expectUser.getLogin(), userTest.getLogin(), "Пользователь некорректно записан в базу." +
                "Ошибка в login");
        assertEquals(expectUser.getName(), userTest.getName(), "Пользователь некорректно записан в базу." +
                "Ошибка в name");
        assertEquals(expectUser.getBirthday(), userTest.getBirthday(), "Пользователь некорректно записан" +
                "в базу.Ошибка в birthday");
    }

    @Test
    public void testGetUsersWithNotEmptyBase() {
        user.setLogin("testGetUserotEmptyBase");
        userService.addUser(user);
        List<User> users = userService.getUsers();

        assertFalse(users.isEmpty(), "Возвращен пустой список пользователей, после добавления.");
    }

    @Test
    public void testUpdateUser() {
        User userTest = User.builder()
                .login("testUpdateUserWithNormalId")
                .email("testCreate@ya.ru")
                .birthday(LocalDate.now())
                .build();
        userTest = userService.addUser(userTest);

        User updateUser = User.builder()
                .id(userTest.getId())
                .login("testUpdateUserWithNormalId")
                .email("testUpdateUserWithNormalId@ya.ru")
                .birthday(LocalDate.now())
                .build();

        updateUser = userService.updateUser(updateUser);

        assertEquals("testUpdateUserWithNormalId", updateUser.getLogin());
    }

    @Test
    public void testAddFriendReturnEmptyListFriends() {
        user.setLogin("testAddFriendReturnEmptyListFriends");
        User userTest = userService.addUser(user);
        userTest = userService.getUserById(userTest.getId());

        assertTrue(userTest.getFriends().isEmpty(), "Сохранено со списком друзей.");
    }
}
