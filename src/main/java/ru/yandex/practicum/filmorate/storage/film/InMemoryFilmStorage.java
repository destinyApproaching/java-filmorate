package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> films = new ArrayList<>();
    private int id = 1;
    private static final LocalDate FIRST_FILM_SESSION = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESC_SIZE = 200;

    @Override
    public List<Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        Optional<Film> film = films.stream()
                .filter(x -> x.getId() == id)
                .findFirst();
        if (film.isPresent()) {
            return film.get();
        }
        throw new FilmNotFoundException(String.format("Фильм с id=%d не существует.", id));
    }

    @Override
    public Film addFilm(Film film) {
        isValid(film);
        if (film.getId() == null) {
            film.setId(id);
            increaseId();
        }
        films.add(film);
        log.info(String.format("Фильм %s добавлен.", film.getName()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        isValid(film);
        for (Film findFilm : films) {
            if (Objects.equals(film.getId(), findFilm.getId())) {
                int index = films.indexOf(findFilm);
                films.set(index, film);
                log.info(String.format("Фильм %s обновлён.", film.getName()));
                return film;
            }
        }
        throw new FilmNotFoundException(String.format("Фильм %s не найден.", film.getName()));
    }

    private void increaseId() {
        id++;
    }

    private void isValid(Film film) {
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > MAX_DESC_SIZE) {
            throw new ValidationException(String.format("Максимальная длина описания — %d символов", MAX_DESC_SIZE));
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_SESSION)) {
            throw new ValidationException("Дата релиза — не раньше " + FIRST_FILM_SESSION);
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
