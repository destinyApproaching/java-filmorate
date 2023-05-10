package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getMpas() {
        List<Mpa> mpas = new ArrayList<>();
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select RATING_ID from RATINGS;");
        while (mpaRows.next()) {
            mpas.add(getMpaById(Integer.parseInt(mpaRows.getString("RATING_ID"))));
        }
        return mpas;
    }

    @Override
    public Mpa getMpaById(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select RATING_ID, RATING_NAME from RATINGS " +
                "where RATING_ID = ?;", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa(
                    Integer.parseInt(mpaRows.getString("RATING_ID")),
                    mpaRows.getString("RATING_NAME")
            );
            log.info("MPA найдет: {} {}", mpa.getId(), mpa.getName());
            return mpa;
        } else {
            log.info("MPA с id={} не найден.", id);
            throw new MpaNotFoundException(String.format("MPA с таким id=%d не существует.", id));
        }
    }
}