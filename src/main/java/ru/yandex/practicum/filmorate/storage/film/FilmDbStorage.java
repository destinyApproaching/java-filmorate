package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private static final LocalDate FIRST_FILM_SESSION = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESC_SIZE = 200;

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from FILMS;");
        while (filmRows.next()) {
            films.add(getFilmById(Integer.parseInt(filmRows.getString("FILM_ID"))));
        }
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "select * from FILMS where FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            sqlQuery = "select * from GENRES as ge right join FILM_GENRE as fg on ge.GENRE_ID = fg.GENRE_ID " +
                    "where fg.FILM_ID = ?;";
            SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery,
                    Integer.parseInt(filmRows.getString("FILM_ID")));
            List<Genre> genres = new ArrayList<>();
            while (genreRows.next()) {
                genres.add(genreDbStorage.getGenreById(
                        Integer.parseInt(genreRows.getString("GENRE_ID"))));
            }
            sqlQuery = "select USER_ID from LIKES where FILM_ID = ?";
            SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
            Set<Integer> likes = new HashSet<>();
            while (likesRows.next()) {
                likes.add(Integer.parseInt(likesRows.getString("USER_ID")));
            }
            Film film = new Film(
                    Integer.parseInt(filmRows.getString("FILM_ID")),
                    filmRows.getString("FILM_NAME"),
                    filmRows.getString("FILM_DESCRIPTION"),
                    LocalDate.parse(filmRows.getString("FILM_RELEASEDATE")),
                    Integer.parseInt(filmRows.getString("FILM_DURATION")),
                    likes,
                    genres,
                    mpaDbStorage.getMpaById(Integer.parseInt(filmRows.getString("RATING_ID"))));
            log.info("Фильм найден: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.warn("Фильм с id={} не найден.", id);
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", id));
        }
    }

    @Override
    public Film addFilm(Film film) {
        isValid(film);
        film.setMpa(mpaDbStorage.getMpaById(film.getMpa().getId()));
        List<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                genreDbStorage.getGenreById(genre.getId());
            }
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        film.setId((int) simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        film.setGenres(updateGenres(genres, film.getId()));
        log.info("Фильм добавлен: {} {}.", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmChecker(film.getId());
        isValid(film);
        updateGenres(film.getGenres(), film.getId());
        String sqlQuery = "update FILMS set FILM_NAME = ?, FILM_DESCRIPTION = ?, FILM_RELEASEDATE = ?, " +
                "FILM_DURATION = ?, RATING_ID = ? where FILM_ID = ?;";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        film.setMpa(mpaDbStorage.getMpaById(film.getMpa().getId()));
        return getFilmById(film.getId());
    }

    @Override
    public void addLike(int id, int userId) {
        jdbcTemplate.update("insert into LIKES (FILM_ID, USER_ID) values (?, ?);", id, userId);
        log.info("Пользователь {} поставил лайк фильму {}.", userId, id);
    }

    @Override
    public void deleteLike(int id, int userId) {
        filmChecker(id);
        userChecker(userId);
        jdbcTemplate.update("delete from LIKES where FILM_ID = ? and USER_ID = ?;", id, userId);
        log.info("Пользователь {} убрал лайк с фильма {}.", userId, id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> films = new ArrayList<>();
        String popularRows = "select * from FILMS f left join LIKES l on f.FILM_ID = l.FILM_ID group by f.FILM_ID " +
                "order by l.FILM_ID desc limit ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(popularRows, count);
        while (sqlRowSet.next()) {
            films.add(getFilmById(Integer.parseInt(sqlRowSet.getString("FILM_ID"))));
        }
        return films;
    }

    private List<Genre> updateGenres(List<Genre> genres, Integer filmId) {
        List<Genre> newGenres = new ArrayList<>();
        String sqlQuery = "delete from FILM_GENRE where FILM_ID = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
        if (genres != null && !genres.isEmpty()) {
            genres = genres.stream()
                    .distinct()
                    .collect(Collectors.toList());
            for (Genre genre : genres) {
                sqlQuery = "insert into FILM_GENRE (FILM_ID, GENRE_ID) values (?, ?);";
                jdbcTemplate.update(sqlQuery, filmId, genre.getId());
                genre = genreDbStorage.getGenreById(genre.getId());
                newGenres.add(genre);
            }
        }
        return newGenres;
    }

    private void filmChecker(int id) {
        String sqlQuery = "select * from FILMS where FILM_ID = ?;";
        SqlRowSet filmId = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmId.next()) {
            log.info("Пользователя с id = {} есть в бд", id);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с id=%d нет в бд", id));
        }
    }

    private void userChecker(int id) {
        String sqlQuery = "select USER_ID from USERS where USER_ID = ?;";
        SqlRowSet userId = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userId.next()) {
            log.info("Пользователя с id = {} есть в бд", id);
        } else {
            throw new UserNotFoundException(String.format("Пользователя с id=%d нет в бд", id));
        }
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
