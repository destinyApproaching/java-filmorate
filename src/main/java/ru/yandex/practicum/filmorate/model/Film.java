package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Slf4j
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Long duration;

    private final List<Integer> likes = new ArrayList<>();

    public Integer getLikesCount() {
        return likes.size();
    }

    public void addLike(User user) {
        if (likes.contains(user.getId())) {
            throw new ValidationException(String.format("Пользователь %s уже лайкнул фильм %s",
                    user.getLogin(), this.getName()));
        }
        likes.add(user.getId());
        log.info("Пользователь {} лайкнул фильм {}", user.getLogin(), this.getName());
    }

    public void deleteLike(User user) {
        if (!likes.contains(user.getId())) {
            throw new ValidationException(String.format("Пользователь %s не лайкал фильм %s",
                    user.getLogin(), this.getName()));
        }
        likes.remove(user.getId());
        log.info("Пользователь {} убрал лайк с фильма {}", user.getLogin(), this.getName());
    }
}
