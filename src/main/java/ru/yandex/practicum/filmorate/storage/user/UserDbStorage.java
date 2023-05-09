package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS;");
        while (userRows.next()) {
            users.add(getUserById(Integer.parseInt(userRows.getString("USER_ID"))));
        }
        return users;
    }

    @Override
    public User getUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where USER_ID = ?;", id);
        if (userRows.next()) {
            SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(
                    "select FRIEND_ID from FRIENDS where (USER_ID = ?);", id);

            Set<Integer> friends = new HashSet<>();
            while (friendsRows.next()) {
                friends.add(Integer.parseInt(friendsRows.getString("FRIEND_ID")));
            }

            User user = new User(
                    Integer.parseInt(userRows.getString("USER_ID")),
                    userRows.getString("USER_EMAIL"),
                    userRows.getString("USER_LOGIN"),
                    userRows.getString("USER_NAME"),
                    LocalDate.parse(userRows.getString("USER_BIRTHDAY")),
                    friends);

            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new UserNotFoundException(String.format("Пользователя с указанным id=%d не существует.", id));
        }
    }

    @Override
    public User addUser(User user) {
        isValid(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        user.setId((int) simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        log.info("Создан пользователь: {} {}", user.getId(), user.getName());
        return user;
    }

    @Override
    public User updateUser(User user) {
        checker(user.getId());
        isValid(user);
        String sqlQuery = "select * from FRIENDS where USER_ID = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, user.getId());
        while (userRows.next()) {
            deleteFriend(user.getId(), userRows.findColumn("FRIEND_ID"));
            deleteFriend(userRows.findColumn("FRIEND_ID"), user.getId());
        }
        sqlQuery = "update USERS set USER_EMAIL = ?, USER_LOGIN = ?, USER_NAME = ?, USER_BIRTHDAY = ?;";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {
        checker(id);
        checker(friendId);
        String sqlQueryToFind = "select * from FRIENDS where USER_ID = ? and FRIEND_ID = ?;";
        SqlRowSet userRows =jdbcTemplate.queryForRowSet(sqlQueryToFind, id, friendId);
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(sqlQueryToFind, friendId, id);
        if (userRows.next()) {
            if (userRows.getString("USER_STATUS").equals("CONFIRMED")) {
                log.info("{} и {} уже друзья.", id, friendId);
            } else log.info("{} уже отправил завяку в друзья {}", id, friendId);
        } else {
            String sqlQueryToInsert = "insert into FRIENDS (USER_ID, FRIEND_ID, FRIEND_STATUS) " +
                    "VALUES (?, ?, ?);";
            if (friendRows.next()) {
                String sqlQueryToUpdate = "update FRIENDS set FRIEND_STATUS = \"CONFIRMED\" " +
                        "where USER_ID = ? and FRIEND_ID = ?;";
                jdbcTemplate.update(sqlQueryToUpdate, friendId, id);

                jdbcTemplate.update(sqlQueryToInsert, id, friendId, "CONFIRMED");
                log.info("{} и {} теперь друзья.", id, friendId);
            } else {
                jdbcTemplate.update(sqlQueryToInsert, id, friendId, "UNCONFIRMED");
                log.info("{} отправил заявку в друзья {}.", id, friendId);
            }
        }
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        checker(id);
        checker(friendId);
        String sqlQueryToFind = "select * from FRIENDS where USER_ID = ? and FRIEND_ID = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQueryToFind, id, friendId);
        if (userRows.next()) {
            String sqlQueryToDelete = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?;";
            if (userRows.getString("FRIEND_STATUS").equals("CONFIRMED")) {
                jdbcTemplate.update(sqlQueryToDelete, id, friendId);
                String sqlQueryToUpdate = "update FRIENDS set FRIEND_STATUS = \"UNCONFIRMED\" " +
                        "where USER_ID = ? and FRIEND_ID = ?;";
                jdbcTemplate.update(sqlQueryToUpdate, friendId, id);
                log.info("{} удалил из друзей {}.", id, friendId);
            } else {
                jdbcTemplate.update(sqlQueryToDelete, id, friendId);
                log.info("{} больше не подписчик {}.", id, friendId);
            }
        } else log.info("{} нет заявки и не находится в друзьях у {}.", id, friendId);
    }

    @Override
    public List<User> getUserFriends(int id) {
        List<User> friends = new ArrayList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from FRIENDS where USER_ID = ?;", id);
        while (userRows.next()) {
            friends.add(getUserById(Integer.parseInt(userRows.getString("FRIEND_ID"))));
        }
        return friends;
    }

    @Override
    public List<User> getMutualFriends(int id, int friendId) {
        List<User> mutualFriends = new ArrayList<>();
        Set<Integer> userFriends = new HashSet<>();
        Set<Integer> friendFriends = new HashSet<>();
        String sqlQuery = "select * from FRIENDS where USER_ID = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        while (userRows.next()) {
            userFriends.add(Integer.parseInt(userRows.getString("FRIEND_ID")));
        }
        userRows = jdbcTemplate.queryForRowSet(sqlQuery, friendId);
        while (userRows.next()) {
            friendFriends.add(Integer.parseInt(userRows.getString("FRIEND_ID")));
        }
        userFriends.retainAll(friendFriends);
        for (Integer userFriend : userFriends) {
            mutualFriends.add(getUserById(userFriend));
        }
        return mutualFriends;
    }

    private void checker(int id) {
        String sqlQuery = "select USER_ID from USERS where USER_ID = ?;";
        SqlRowSet userId = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userId.next()) {
            log.info("Пользователя с id = {} есть в бд", id);
        } else {
            throw new UserNotFoundException(String.format("Пользователя с id=%d нет в бд", id));
        }
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
