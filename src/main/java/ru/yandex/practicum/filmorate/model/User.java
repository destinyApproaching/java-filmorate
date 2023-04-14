package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Slf4j
public class User {
    private Integer id;
    @NonNull
    @NotBlank
    @Email
    private String email;
    @NonNull
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();

    public void addFriend(User user) {
        if (this.equals(user)) {
            throw new ValidationException("Вы не можете добавить в друзья самого себя.");
        }
        if (friends.contains(user.getId())) {
            throw new ValidationException(String.format("Пользователь %s уже у вас в друзьях.", user.login));
        }
        friends.add(user.getId());
        user.getFriends().add(this.getId());
        log.info("{} и {} теперь друзья.", this.login, user.login);
    }

    public void deleteFriend(User user) {
        if (this.equals(user)) {
            throw new ValidationException("Вы не можете удалить из друзей самого себя.");
        }
        if (!friends.contains(user.getId())) {
            throw new ValidationException(String.format("Пользователь %s нет у вас в друзьях.", user.login));
        }
        friends.remove(user.getId());
        user.getFriends().remove(this.getId());
        log.info("{} и {} больше не друзья.", this.login, user.login);
    }
}
