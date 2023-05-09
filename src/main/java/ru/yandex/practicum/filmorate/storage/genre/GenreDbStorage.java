package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select  * from GENRES;");
        while (genreRows.next()) {
            genres.add(getGenreById(Integer.parseInt(genreRows.getString("GENRE_ID"))));
        }
        return genres;
    }

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from GENRES where GENRE_ID = ?;", id);
        if (genreRows.next()) {
            Genre genre = new Genre(
                    Integer.parseInt(genreRows.getString("GENRE_ID")),
                    genreRows.getString("GENRE_NAME")
            );
            log.info("Жанр найден: {} {}", genre.getId(), genre.getName());
            return genre;
        } else {
            log.info("Жанра с id={} не найдено.", id);
            throw new GenreNotFoundException(String.format("Пользователя с указанным id=%d не существует.", id));
        }
    }
}