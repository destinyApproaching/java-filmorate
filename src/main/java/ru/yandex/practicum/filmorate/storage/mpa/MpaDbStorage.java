package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select RATING_ID, RATING_NAME from RATINGS;");
        while (mpaRows.next()) {
            mpas.add(new Mpa(
                    Integer.parseInt(mpaRows.getString("RATING_ID")),
                    mpaRows.getString("RATING_NAME")));
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

    @Override
    public Map<Integer, Mpa> getMpasWithId() {
        Map<Integer, Mpa> mpasWithId = new HashMap<>();
        String sqlQuery = "select FILM_ID, R.RATING_ID, RATING_NAME from RATINGS R join FILMS F on R.RATING_ID = F.RATING_ID;";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery);
        while (mpaRows.next()) {
            mpasWithId.put(
                    Integer.parseInt(mpaRows.getString("FILM_ID")),
                    new Mpa(Integer.parseInt(mpaRows.getString("RATING_ID")),
                            mpaRows.getString("RATING_NAME")));
        }
        return mpasWithId;
    }
}