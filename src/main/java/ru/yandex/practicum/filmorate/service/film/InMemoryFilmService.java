package ru.yandex.practicum.filmorate.service.film;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.comparator.FilmComparator;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InMemoryFilmService implements FilmService {
    private final FilmComparator filmComparator = new FilmComparator();
    public static final Integer LIMIT = 10;
    @Getter
    private final InMemoryUserStorage inMemoryUserStorage;
    @Getter
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public InMemoryFilmService(InMemoryUserStorage inMemoryUserStorage, InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    @Override
    public Film getFilm(int id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return inMemoryFilmStorage.getFilms().stream()
                .sorted(filmComparator)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(int id, int userId) {
        Film film = inMemoryFilmStorage.getFilmById(id);
        User user = inMemoryUserStorage.getUserById(userId);
        film.addLike(user);
    }

    @Override
    public void deleteLike(int id, int userId) {
        Film film = inMemoryFilmStorage.getFilmById(id);
        User user = inMemoryUserStorage.getUserById(userId);
        film.deleteLike(user);
    }
}
