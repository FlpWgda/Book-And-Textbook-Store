package com.service.store.api;

import com.service.store.dao.*;
import com.service.store.entity.*;
import com.service.store.recommendation.RecommendationEngine;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

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
            if(itemOptional.get().isVisible()){
                return new ResponseEntity<Item>(itemOptional.get(),HttpStatus.OK);
            }
        }
        return new ResponseEntity<Item>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/api/item/{itemId}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteItemById(@RequestAttribute Claims claims, @PathVariable("itemId") Integer itemId){

        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if(itemOptional.isPresent()){
            itemOptional.get().setVisible(false);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
    @RequestMapping(value = "/api/item/myItems",
            method = RequestMethod.GET)
    public ResponseEntity<List<Item>> myItems(@RequestAttribute Claims claims){
        User user = userRepository.findById((String) claims.get("login")).get();
        List<Item> listOfItems = new ArrayList<Item>();
        for(Item i: user.getItems()){
            if(i.isVisible()){
                listOfItems.add(i);
            }
        }
        return new ResponseEntity<>(listOfItems,HttpStatus.OK);
    }

    @RequestMapping(value = "/api/item/search",
            method = RequestMethod.GET)
    public ResponseEntity<List<Item>> searchItems(@RequestParam(value = "keyword", required = true) String keyword){

        List<Item> list1 = itemRepository.findByNameContainingOrPublishingHouseContainingOrAuthors_NameContainingOrCategories_GenreNameContaining(keyword, keyword, keyword, keyword);
        return new ResponseEntity<>(list1,HttpStatus.OK);
    }

    @RequestMapping(value = "/api/item/recommended/{amount}",
            method = RequestMethod.GET)
    public ResponseEntity<List<Item>> findRecommended(@RequestAttribute Claims claims, @PathVariable("amount") Integer amount){
        User user = userRepository.findById((String) claims.get("login")).get();
        List<Item> listOfItems = new ArrayList<>();
        try {
            if(!RecommendationEngine.userIds.containsKey(user.getLogin())){
                long userId = RecommendationEngine.userIds.size();
                RecommendationEngine.userIds.put(user.getLogin(),userId);
            }
            List<RecommendedItem> recommendedGenres = RecommendationEngine.recommendGenres(user.getLogin(),amount);
            System.out.println("Recommended genres: " + recommendedGenres.toString());
            System.out.println(recommendedGenres.size());
            float totalRecommendationValue = 0.0f;

            for(RecommendedItem r: recommendedGenres){
                totalRecommendationValue += r.getValue();
            }
            for(int i = 0; i < amount; i++){
                int idx = 0;
                for (double r = Math.random() * totalRecommendationValue; idx < recommendedGenres.size() - 1; ++idx) {
                    r -= recommendedGenres.get(idx).getValue();
                    if (r <= 0.0) break;
                }
                RecommendedItem myRandomItem = recommendedGenres.get(idx);
                System.out.println("ID: " + myRandomItem.getItemID() + " name: " + RecommendationEngine.genreNameById(myRandomItem.getItemID()));
                List<Item> itemList = itemRepository.findByCategories_GenreName(RecommendationEngine.genreNameById(myRandomItem.getItemID()));
                if(itemList.isEmpty()){
                    i--;
                }
                else {
                    Collections.shuffle(itemList);
                    listOfItems.add(itemList.get(0));
                }
            }

        } catch (TasteException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(listOfItems,HttpStatus.OK);
    }



}
