package com.service.store.api;

import com.service.store.dao.*;
import com.service.store.entity.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class ItemApi {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ImageUrlRepository imageUrlRepository;

    @RequestMapping(value = "/api/item",
            method = RequestMethod.POST)
    public ResponseEntity<Item> addItem(@RequestAttribute Claims claims, @RequestBody Item item) {

        for(Author a: item.getAuthors()){
            Optional<Author> authorOptional = authorRepository.findById(a.getName());
            if(!authorOptional.isPresent()){
                authorRepository.save(a);
            }
        }
        for(Category c:item.getCategories()){
            Optional<Category> categoryOptional = categoryRepository.findById(c.getGenreName());
            if(!categoryOptional.isPresent()){
                categoryRepository.save(c);
            }
        }
        for(ImageUrl i:item.getImageUrls()){
            Optional<ImageUrl> imageUrlOptional = imageUrlRepository.findById(i.getUrl());
            if(!imageUrlOptional.isPresent()){
                imageUrlRepository.save(i);
            }
        }
        User user = userRepository.findById((String) claims.get("login")).get();
        List<Item> listOfItems = user.getItems();
        listOfItems.add(item);
        user.setItems(listOfItems);

        item.setDateAdded(Timestamp.valueOf(LocalDateTime.now()));
        item.setVisible(true);
        itemRepository.save(item);
        return new ResponseEntity<Item>(item,HttpStatus.OK);
    }

    @RequestMapping(value = "/api/item/{itemId}",
            method = RequestMethod.GET)
    public ResponseEntity<Item> getItemById(@PathVariable("itemId") Integer itemId){

        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if(itemOptional.isPresent()){
            return new ResponseEntity<Item>(itemOptional.get(),HttpStatus.OK);
        }
        return new ResponseEntity<Item>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/api/item/{itemId}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteItemById(@RequestAttribute Claims claims, @PathVariable("itemId") Integer itemId){

        itemRepository.deleteById(itemId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
    @RequestMapping(value = "/api/item/myItems",
            method = RequestMethod.GET)
    public ResponseEntity<List<Item>> myItems(@RequestAttribute Claims claims){
        User user = userRepository.findById((String) claims.get("login")).get();
        List<Item> listOfItems = user.getItems();
        return new ResponseEntity<>(listOfItems,HttpStatus.OK);
    }

    @RequestMapping(value = "/api/item/search",
            method = RequestMethod.GET)
    public ResponseEntity<List<Item>> searchItems(@RequestAttribute Claims claims, @RequestParam(value = "keyword", required = true) String keyword){
        User user = userRepository.findById((String) claims.get("login")).get();
        List<Item> listOfItems = user.getItems();
        return new ResponseEntity<>(listOfItems,HttpStatus.OK);
    }

    @RequestMapping(value = "/api/item/recommended/{amount}",
            method = RequestMethod.GET)
    public ResponseEntity<List<Item>> findRecommended(@RequestAttribute Claims claims, @PathVariable("amount") Integer amount){
        User user = userRepository.findById((String) claims.get("login")).get();
        List<Item> listOfItems = user.getItems();
        return new ResponseEntity<>(listOfItems,HttpStatus.OK);
    }



}
