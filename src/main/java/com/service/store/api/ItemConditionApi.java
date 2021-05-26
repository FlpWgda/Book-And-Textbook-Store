package com.service.store.api;

import com.service.store.entity.Item;
import com.service.store.entity.ItemCondition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin
public class ItemConditionApi {

    @RequestMapping(value = "/api/itemCondition/listAll",
            method = RequestMethod.GET)
    public ResponseEntity<List<ItemCondition>> listItemConditions(){

        return new ResponseEntity<>(Arrays.asList(ItemCondition.values()),HttpStatus.OK);
    }
}
