package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Component
@Slf4j
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select GENRE_ID, GENRE_NAME from GENRES;");
        while (genreRows.next()) {
            genres.add(new Genre(
                    Integer.parseInt(genreRows.getString("GENRE_ID")),
                    genreRows.getString("GENRE_NAME")));
        }
        return genres;
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlQuery = "select GENRE_ID, GENRE_NAME from GENRES where GENRE_ID = ?;";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (genreRows.next()) {
            Genre genre = new Genre(
                    Integer.parseInt(genreRows.getString("GENRE_ID")),
                    genreRows.getString("GENRE_NAME")
            );
            log.info("Жанр найден: {} {}", genre.getId(), genre.getName());
            return genre;
        } else {
            log.info("Жанра с id={} не найдено.", id);
            throw new GenreNotFoundException(String.format("Жанр с указанным id=%d не существует.", id));
        }
    }

    @Override
    public Map<Integer, List<Genre>> getFilmGenre() {
        Map<Integer, List<Genre>> filmGenres = new HashMap<>();
        String sqlQuery = "select FILM_ID, GE.GENRE_ID, GENRE_NAME from GENRES GE " +
                "JOIN FILM_GENRE FG on GE.GENRE_ID = FG.GENRE_ID;";
        SqlRowSet filmGenreRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (filmGenreRows.next()) {
            Genre genre = new Genre(
                    Integer.parseInt(filmGenreRows.getString("GENRE_ID")),
                    filmGenreRows.getString("GENRE_NAME"));
            List<Genre> genres;
            if (!filmGenres.containsKey(Integer.parseInt(filmGenreRows.getString("FILM_ID")))) {
                genres = new ArrayList<>();
                genres.add(genre);
                filmGenres.put(
                        Integer.parseInt(filmGenreRows.getString("FILM_ID")),
                        genres);
            } else {
                 genres = filmGenres.get(Integer.parseInt(filmGenreRows.getString("FILM_ID")));
                 if (!genres.contains(genre)) {
                     genres.add(genre);
                     filmGenres.put(Integer.parseInt(filmGenreRows.getString("FILM_ID")), genres);
                 }
            }
        }
        return filmGenres;
    }

    @Override
    public List<Genre> getGenresByFilmId(int filmId) {
        List<Genre> genres = new ArrayList<>();
        String sqlQuery = "select * from FILM_GENRE fg join GENRES G2 on G2.GENRE_ID = fg.GENRE_ID where FILM_ID = ?;";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        while (genreRows.next()) {
            Genre genre = new Genre(
                    Integer.parseInt(genreRows.getString("GENRE_ID")),
                    genreRows.getString("GENRE_NAME"));
            if (!genres.contains(genre)) {
                genres.add(genre);
            }
        }
        return genres;
    }
}
