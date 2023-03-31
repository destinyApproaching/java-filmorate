package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.expection.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final List<Film> films = new ArrayList<>();
    private int id = 1;

    @GetMapping
    public List<Film> getFilms() {
        return films;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        isValid(film);
        if (film.getId() == null) {
            film.setId(id);
            increaseId();
        }
        films.add(film);
        logDebug("Фильм успешно добавлен");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        isValid(film);
        for (Film findFilm : films) {
            if (Objects.equals(film.getId(), findFilm.getId())) {
                int index = films.indexOf(findFilm);
                films.set(index, film);
                logDebug("Фильм успешно обновлён");
                return film;
            }
        }
        throw new ValidationException("Ошибка");
    }

    private void increaseId() {
        id++;
    }

    private void logDebug(String string) {
        log.debug(string);
    }



    private void isValid(Film film) {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
