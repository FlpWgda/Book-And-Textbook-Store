package com.service.store.api;

import com.service.store.dao.ItemRepository;
import com.service.store.dao.UserRepository;
import com.service.store.entity.Item;
import com.service.store.entity.User;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class BidApi {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/api/bid/{itemId}",
            method = RequestMethod.POST)
    public ResponseEntity<Item> postNewBid(@RequestAttribute Claims claims, @PathVariable("itemId") Integer itemId, @RequestBody Double bid){
        Optional<Item> itemOptional = itemRepository.findById(itemId);

        if(itemOptional.isPresent()){
            Item item = itemOptional.get();
            item.setHighestBid(bid);
            itemRepository.save(item);
            return new ResponseEntity<>(item, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/api/bid/allBids",
            method = RequestMethod.GET)
    public ResponseEntity<List<Item>> findAllBids(@RequestAttribute Claims claims){

        User user = userRepository.findById((String) claims.get("login")).get();
        List<Item> listOfItemsWithBids = new ArrayList<>();
        for(Item i: user.getItems()){
            if(i.getHighestBid() != null){
                listOfItemsWithBids.add(i);
            }
        }
        return new ResponseEntity<>(listOfItemsWithBids, HttpStatus.OK);
    }

    @RequestMapping(value = "/api/bid/acceptBid/{itemId}",
            method = RequestMethod.POST)
    public ResponseEntity<Item> acceptBid(@RequestAttribute Claims claims, @PathVariable("itemId") Integer itemId){

        User user = userRepository.findById((String) claims.get("login")).get();

        for(Item i: user.getItems()){
            if(i.getItemId() == itemId){
                i.setPrice(i.getHighestBid());
                i.setHighestBid(null);
                itemRepository.save(i);
                return new ResponseEntity<>(i, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
