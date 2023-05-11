package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

public interface GenreStorage {
    List<Genre> getGenres();

    Genre getGenreById(int id);

    List<Genre> getGenresByFilmId(int filmId);

    Map<Integer, List<Genre>> getFilmGenre();
}