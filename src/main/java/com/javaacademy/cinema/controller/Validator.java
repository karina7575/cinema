package com.javaacademy.cinema.controller;

import com.javaacademy.cinema.exception.InvalidAuthorization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class Validator {
    @Value("${app.admin_token}")
    String trueToken;
    @Value("${app.admin_password}")
    String truePassword;

    public void checkAdmin(String token, String password) {
        System.out.println(trueToken);
        System.out.println(truePassword);
        if (!Objects.equals(token, trueToken) || !Objects.equals(password, truePassword)) {
            throw new InvalidAuthorization("Неверный токен или пароль.");
        }
    }
}
