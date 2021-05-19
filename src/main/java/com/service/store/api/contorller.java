package com.service.store.api;

import com.service.store.entity.Genre;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Date;

public class contorller {

    @PostMapping("/user/login")
    public String login(@RequestBody Genre user) {
        Long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(user.getGenreName()) // 1
                .claim("roles", "user") // 2
                .setIssuedAt(new Date(now)) // 3
                .setExpiration(new Date(now + 10000)) // 4
                .signWith(SignatureAlgorithm.HS512, "secretkey").compact(); // 5
    }
    @RequestMapping(value = "/user/{username}",
            method = RequestMethod.GET)
    public String test(){
        return "test";
    }

}
