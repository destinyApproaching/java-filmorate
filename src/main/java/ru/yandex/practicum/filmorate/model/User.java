package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@Slf4j
public class User {
    private Integer id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private Set<Integer> friends;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("USER_EMAIL", email);
        values.put("USER_LOGIN", login);
        values.put("USER_NAME", name);
        values.put("USER_BIRTHDAY", birthday);
        return values;
    }
}