package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {
    private final FilmService filmService;
    private final UserService userService;

    Film film = Film.builder()
            .id(-1)
            .name("Name")
            .description("Desc")
            .releaseDate(LocalDate.now())
            .duration(120)
            .mpa(Mpa.builder().id(1).build())
            .build();


    @Test
    public void testCreateFilmMustReturnUserWithNewId() {
        film.setName("testCreateFilmMustReturnUserWithNewId");
        film = filmService.addFilm(film);
        assertNotEquals(-1, (int) film.getId(), "Фильм не записан в базу, не присвоен id.");
    }

    @Test
    public void testGetFilmsWithNotEmptyBase() {
        film.setName("testGetFilmsWithNotEmptyBase");
        filmService.addFilm(film);
        List<Film> films = filmService.getFilms();
        assertFalse(films.isEmpty(), "Возвращен пустой список фильмов, после добавления.");
    }

    @Test
    public void testFindUserByNormalId() {
        film.setName("testFindUserByNormalId");
        Film expectedFilm = filmService.addFilm(film);

        assertEquals(expectedFilm.getName(), film.getName(), "Фильм некорректно записан в базу. Ошибка в name");
        assertEquals(expectedFilm.getDescription(), film.getDescription(), "Фильм некорректно записан в" +
                "базу. Ошибка в description");
    }

    @Test
    public void testFindUserByWrongId() {
        final FilmNotFoundException exception = assertThrows(
                FilmNotFoundException.class,
                () -> filmService.getFilmById(-1)
        );

        assertEquals("Фильм с идентификатором -1 не найден.", exception.getMessage());
    }

    @Test
    public void testUpdateFilmId() {
        film.setName("testUpdateFilmNormalId");
        film = filmService.addFilm(film);
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder().id(1).build());
        genres.add(Genre.builder().id(3).build());
        film.setMpa(Mpa.builder().id(2).build());
        film.setGenres(genres);
        filmService.addFilm(film);
        Film expectedFilm = filmService.getFilmById(film.getId());

        assertEquals(2, expectedFilm.getMpa().getId(), "Не выполнено обновления фильма. Не записан рейтинг.");
        assertEquals(2, expectedFilm.getGenres().size(), "Не выполнено обновления фильма. Не записаны жанры.");
    }

    @Test
    void testAddLike() {
        film.setName("testForAddLIke");
        film = filmService.addFilm(film);
        User userTest = User.builder()
                .login("testAddLike")
                .email("testCreate@ya.ru")
                .birthday(LocalDate.now())
                .build();
        userTest = userService.addUser(userTest);
        filmService.addLike(film.getId(), userTest.getId());
        film = filmService.getFilmById(film.getId());

        assertEquals(1, film.getLikes().size(), "Лайк фильму не поставлен.");
    }

    @Test
    void testDeleteLike() {
        film.setName("testDeleteLike");
        film = filmService.addFilm(film);
        User userTest = User.builder()
                .login("testDeleteLike")
                .email("testCreate@ya.ru")
                .birthday(LocalDate.now())
                .build();
        userTest = userService.addUser(userTest);
        filmService.addLike(film.getId(), userTest.getId());
        filmService.deleteLike(film.getId(), userTest.getId());
        film = filmService.getFilmById(film.getId());
        assertTrue(film.getLikes().isEmpty(), "Лайк фильму не удален.");
    }

    @Test
    void testGetPopularFilms() {
        film = filmService.addFilm(film);
        User userTest = User.builder()
                .login("testLogin")
                .email("testCreate@ya.ru")
                .birthday(LocalDate.now())
                .build();
        userTest = userService.addUser(userTest);
        filmService.addLike(film.getId(), userTest.getId());
        film.setId(-1);
        film.setName("Name2");
        filmService.addFilm(film);

        List<Film> films = filmService.getPopularFilms(2);
        assertEquals(2, films.size(), "Неправильно выдает популярные фильмы");
    }
}
