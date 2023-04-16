package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film getFilmById(int id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);
}
