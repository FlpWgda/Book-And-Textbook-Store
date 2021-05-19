package com.service.store.api;

import com.service.store.dao.ItemRepository;
import com.service.store.entity.Genre;
import com.service.store.entity.Item;
import com.service.store.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ItemApi {

    @Autowired
    ItemRepository itemRepository;

    @RequestMapping(value = "/item/",
            method = RequestMethod.POST)
    public ResponseEntity<Void> addItem(@RequestAttribute Claims claims, @RequestBody Item item) {
        User user = (User) claims.get("user");
        item.setUser(user);
        item.setDateAdded(Timestamp.valueOf(LocalDateTime.now()));
        itemRepository.save(item);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @RequestMapping(value = "/test",
            method = RequestMethod.GET)
    public String test(){
        return "test";
    }

}
