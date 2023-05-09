package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmDaoService implements FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmDaoService(FilmDbStorage filmDbStorage) {
        this.filmStorage = filmDbStorage;
    }

    @Override
    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @Override
    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    public void addLike(int id, int userId) {
        filmStorage.addLike(id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        filmStorage.deleteLike(id, userId);
    }
}