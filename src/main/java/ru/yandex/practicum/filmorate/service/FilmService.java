package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getFilms();

    Film getFilmById(int id);

    List<Film> getPopularFilms(int count);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);
}
