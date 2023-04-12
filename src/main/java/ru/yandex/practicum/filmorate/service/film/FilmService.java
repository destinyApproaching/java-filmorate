package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film getFilm(int id);

    List<Film> getPopularFilms(int count);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);
}
