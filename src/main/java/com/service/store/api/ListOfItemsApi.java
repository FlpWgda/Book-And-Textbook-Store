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
import java.util.ArrayList;
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
        Optional<Item> itemOptional = itemRepository.findById(itemId);

        if(itemOptional.isPresent()){
            ListOfItems basket = null;
            for(ListOfItems l: user.getListsOfItems()){
                if(l.isBasket()){
                    basket = l;
                    break;
                }
            }
            List<Item> basketItems = basket.getItems();
            for(Item i:basketItems){
                if(i.equals(itemOptional.get())){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
            basketItems.add(itemOptional.get());
            listOfItemsRepository.save(basket);
            return new ResponseEntity<>(basket, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/api/list/removeFromBasket/{itemId}",
            method = RequestMethod.DELETE)
    public ResponseEntity<ListOfItems> removeFromBasket(@RequestAttribute Claims claims, @PathVariable("itemId") Integer itemId) {

        User user = userRepository.findById((String) claims.get("login")).get();


        ListOfItems basket = null;
        for(ListOfItems l: user.getListsOfItems()){
            if(l.isBasket()){
                basket = l;
                break;
            }
        }
        List<Item> basketItems = basket.getItems();
        for(Item i:basketItems){
            if(i.getItemId() == itemId){
                basketItems.remove(i);
                listOfItemsRepository.save(basket);
                return new ResponseEntity<>(basket,HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/api/list/getBasket",
            method = RequestMethod.GET)
    public ResponseEntity<ListOfItems> getBasket(@RequestAttribute Claims claims) {

        User user = userRepository.findById((String) claims.get("login")).get();

        ListOfItems basket = null;
        for(ListOfItems l: user.getListsOfItems()){
            if(l.isBasket()){
                basket = l;
                List<Item> newItemList = new ArrayList<>();
                for(Item i:l.getItems()){
                    if(i.isVisible()){
                        newItemList.add(i);
                    }
                }
                l.setItems(newItemList);
                listOfItemsRepository.save(l);
                break;
            }
        }
        return new ResponseEntity<>(basket,HttpStatus.OK);
    }

    @RequestMapping(value = "/api/list",
            method = RequestMethod.POST)
    public ResponseEntity<ListOfItems> addNewList(@RequestAttribute Claims claims, @RequestBody ListOfItems listOfItems) {

        User user = userRepository.findById((String) claims.get("login")).get();
        List<ListOfItems> listOfItemsList = user.getListsOfItems();
        listOfItems.setBasket(false);
        listOfItems.setDateAdded(Timestamp.valueOf(LocalDateTime.now()));
        listOfItemsList.add(listOfItems);
        user.setListsOfItems(listOfItemsList);
        userRepository.save(user);

        return new ResponseEntity<>(listOfItems,HttpStatus.OK);
    }
    @RequestMapping(value = "/api/list/findAll",
            method = RequestMethod.GET)
    public ResponseEntity<List<ListOfItems>> getAllLists(@RequestAttribute Claims claims){
        User user = userRepository.findById((String) claims.get("login")).get();
        List<ListOfItems> listOfItemsList = new ArrayList<>();
        for(ListOfItems l:user.getListsOfItems()){
            if(!l.getName().equals("basket")){
                listOfItemsList.add(l);
            }
        }
        return new ResponseEntity<>(listOfItemsList,HttpStatus.OK);

    }
    @RequestMapping(value = "/api/list/{listId}",
            method = RequestMethod.GET)
    public ResponseEntity<ListOfItems> getListById(@RequestAttribute Claims claims, @PathVariable Integer listId) {

        User user = userRepository.findById((String) claims.get("login")).get();
        List<ListOfItems> listOfItemsList = user.getListsOfItems();
        for(ListOfItems l: listOfItemsList){
            if(l.getListOfItemsId() == listId && !l.isBasket()){
                List<Item> newItemList = new ArrayList<>();
                for(Item i:l.getItems()){
                    if(i.isVisible()){
                        newItemList.add(i);
                    }
                }
                l.setItems(newItemList);
                listOfItemsRepository.save(l);
                return new ResponseEntity<>(l,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/api/list/{listId}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeListById(@RequestAttribute Claims claims, @PathVariable Integer listId) {

        User user = userRepository.findById((String) claims.get("login")).get();
        List<ListOfItems> listOfItemsList = user.getListsOfItems();
        for(ListOfItems l: listOfItemsList){
            if(l.getListOfItemsId() == listId && !l.isBasket()){
                listOfItemsList.remove(l);
                listOfItemsRepository.delete(l);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/api/list/addToList/{itemId}",
            method = RequestMethod.POST)
    public ResponseEntity<ListOfItems> addToList(@RequestAttribute Claims claims, @PathVariable("itemId") Integer itemId, @RequestParam(value = "listName", required = true) String listName) {

        User user = userRepository.findById((String) claims.get("login")).get();
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        ListOfItems listOfItems = null;
        for(ListOfItems l:user.getListsOfItems()){
            if(l.getName().equals(listName)){
                listOfItems = l;
                break;
            }
        }
        if(listOfItems == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(itemOptional.isPresent() && listOfItems != null){
            for(Item i:listOfItems.getItems()){
                if(i.equals(itemOptional.get())){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
            List<Item> listItems = listOfItems.getItems();
            listItems.add(itemOptional.get());
            listOfItemsRepository.save(listOfItems);
            return new ResponseEntity<>(listOfItems, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @RequestMapping(value = "/api/list/removeFromList/{itemId}",
            method = RequestMethod.DELETE)
    public ResponseEntity<ListOfItems> removeFromList(@RequestAttribute Claims claims, @PathVariable("itemId") Integer itemId, @RequestParam(value = "listName", required = true) String listName) {

        User user = userRepository.findById((String) claims.get("login")).get();
        ListOfItems listOfItems = null;
        for(ListOfItems l:user.getListsOfItems()){
            if(l.getName().equals(listName)){
                listOfItems = l;
                break;
            }
        }
        if(listOfItems == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Item> itemList = listOfItems.getItems();
        for(Item i:itemList){
            if(i.getItemId() == itemId){
                itemList.remove(i);
                listOfItemsRepository.save(listOfItems);
                return new ResponseEntity<>(listOfItems,HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
