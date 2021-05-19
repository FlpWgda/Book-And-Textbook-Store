package com.service.store.security;

import com.service.store.dao.UserRepository;
import com.service.store.entity.Role;
import com.service.store.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
public class SecurityApi {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/logIn",
            method = RequestMethod.POST)
    public ResponseEntity<Map<String,String>> login(@RequestBody Map<String,String> userInfo) {
        long currentTimeMillis = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(userInfo.get("login"))
                .claim("user",userInfo.get("login"))
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + 2000000))
                .signWith(JwtFilter.secretKey)
                .compact();
        Map<String,String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        return new ResponseEntity(tokenMap, HttpStatus.OK);
    }
    @RequestMapping(value = "/register",
            method = RequestMethod.POST)
    public ResponseEntity<String> register(@RequestBody User user){
        System.out.println("Hello");
        Optional<User> tempUser = userRepository.findById(user.getLogin());
        if(tempUser.isPresent()){
            return new ResponseEntity<>("Username already taken",HttpStatus.CONFLICT);
        }
        else{
            user.setDateOfRegistration(Timestamp.valueOf(LocalDateTime.now()));
            user.setRole(Role.REGULAR_USER);
            userRepository.save(user);
            return new ResponseEntity<>("Successfully created user account", HttpStatus.OK);
        }
    }
}
