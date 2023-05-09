package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.MpaDaoService;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaDaoService mpaDaoService) {
        this.mpaService = mpaDaoService;
    }

    @GetMapping
    public List<Mpa> getMpas() {
        return mpaService.getMpas();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@RequestBody @PathVariable int id) {
        return mpaService.getMpaById(id);
    }
}