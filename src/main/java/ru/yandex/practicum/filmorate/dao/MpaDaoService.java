package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
public class MpaDaoService implements MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaDaoService(MpaDbStorage mpaDbStorage) {
        this.mpaStorage = mpaDbStorage;
    }

    @Override
    public List<Mpa> getMpas() {
        return mpaStorage.getMpas();
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }
}