package com.service.store.api;

import com.service.store.dao.ListOfItemsRepository;
import com.service.store.dao.UserRepository;
import com.service.store.entity.Item;
import com.service.store.entity.ListOfItems;
import com.service.store.entity.OrderInfo;
import com.service.store.entity.User;
import com.service.store.payments.PaymentService;
import io.jsonwebtoken.Claims;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@RestController
@CrossOrigin
public class OrderInfoApi {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ListOfItemsRepository listOfItemsRepository;

    @RequestMapping(value = "/api/list/buy",
            method = RequestMethod.POST)
    public ResponseEntity<ListOfItems> buy(@RequestAttribute Claims claims) throws IOException, InterruptedException {

        User user = userRepository.findById((String) claims.get("login")).get();

        PaymentService paymentService = new PaymentService();
        String token = paymentService.getToken();

        String redirectUri = paymentService.makePayment(token,user);

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUri)).build();

    }
    @RequestMapping(value = "/api/order/findAll",
            method = RequestMethod.GET)
    public ResponseEntity<List<OrderInfo>> getAllOrders(@RequestAttribute Claims claims){
        User user = userRepository.findById((String) claims.get("login")).get();
        return new ResponseEntity<>(user.getOrderInfos(),HttpStatus.OK);
    }


    @RequestMapping(value = "/stateOfOrder",
            method = RequestMethod.POST)
    public void stateOfOrder(@RequestBody RequestBody requestBody){

    }

}
