package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreDaoService implements GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreDaoService(GenreDbStorage genreDbStorage) {
        this.genreStorage = genreDbStorage;
    }

    @Override
    public List<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    @Override
    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }
}
