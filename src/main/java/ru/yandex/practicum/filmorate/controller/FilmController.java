package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.InMemoryFilmService;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final InMemoryFilmService inMemoryFilmService;

    @Autowired
    public FilmController(InMemoryFilmService inMemoryFilmService) {
        this.inMemoryFilmService = inMemoryFilmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return inMemoryFilmService.getInMemoryFilmStorage().getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@RequestBody @PathVariable int id) {
        return inMemoryFilmService.getFilm(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestBody @RequestParam Optional<Integer> count) {
        if (count.isPresent()) {
            return inMemoryFilmService.getPopularFilms(count.get());
        } else {
            return inMemoryFilmService.getPopularFilms(InMemoryFilmService.LIMIT);
        }
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmService.getInMemoryFilmStorage().addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return inMemoryFilmService.getInMemoryFilmStorage().updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        inMemoryFilmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        inMemoryFilmService.deleteLike(id, userId);
    }
}