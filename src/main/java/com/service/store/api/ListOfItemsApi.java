package com.service.store.api;

import com.service.store.dao.*;
import com.service.store.entity.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class ListOfItemsApi {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ListOfItemsRepository listOfItemsRepository;

    @RequestMapping(value = "/api/list/addToBasket/{itemId}",
            method = RequestMethod.POST)
    public ResponseEntity<ListOfItems> addToBasket(@RequestAttribute Claims claims, @PathVariable("itemId") Integer itemId) {

        User user = userRepository.findById((String) claims.get("login")).get();
        Optional<Item> item = itemRepository.findById(itemId);

        if(item.isPresent()){
            ListOfItems basket = null;
            for(ListOfItems l: user.getListsOfItems()){
                if(l.isBasket()){
                    basket = l;
                    break;
                }
            }
            List<Item> basketItems = basket.getItems();
            basketItems.add(item.get());
            listOfItemsRepository.save(basket);
            return new ResponseEntity<>(basket, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
