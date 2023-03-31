package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class FilmTests extends FilmorateApplicationTests {
    private FilmController filmController;

    private final Film film = Film.builder()
            .id(1)
            .name("Валидный фильм")
            .description("Короткое описание, верная дата релиза и неотрицательная длительность")
            .releaseDate(LocalDate.of(1994, 9, 10))
            .duration(142L)
            .build();

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void shouldAddFilmWhenFilmIsValid() {
        log.debug("Тест на добавление валидного фильма");
        filmController.addFilm(film);
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    public void shouldUpdateFilmWhenFilmIsValid() {
        log.debug("Тест на обновление валидного фильма");
        filmController.addFilm(film);
        film.setName("Обновлённый фильм");
        filmController.updateFilm(film);
        assertEquals(film.getName(), filmController.getFilms().get(0).getName());
    }

    @Test
    public void shouldAddFilmWhenNameIsEmpty() {
        log.debug("Тест на добавление фильма без названия");
        film.setName("");
        filmController.addFilm(film);
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void shouldAddFilmWhenDescriptionLengthMoreThan200Symbols() {
        log.debug("Тест на добавление фильма с названием больше 200 символов");
        film.setDescription("Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены и её любовника. Оказавшись в" +
                "тюрьме под названием Шоушенк, он сталкивается с жестокостью и беззаконием, царящими по обе стороны" +
                "решётки. Каждый, кто попадает в эти стены, становится их рабом до конца жизни. Но Энди, обладающий" +
                "живым умом и доброй душой, находит подход как к заключённым, так и к охранникам, добиваясь их" +
                "особого к себе расположения.");
        filmController.addFilm(film);
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void shouldAddFilmWhenReleaseDateIsBeforeThan1895Year() {
        log.debug("Тест на добавление фильма вышедшего раньше 28 декабря 1895 года");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        filmController.addFilm(film);
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    public void shouldAddFilmWhenDurationIsNegativeOrZeroNumber() {
        log.debug("Тест на добавление фильма с отрицательной или нулевой длительностью");
        film.setDuration(0L);
        filmController.addFilm(film);
        assertEquals(0, filmController.getFilms().size());
    }
}
