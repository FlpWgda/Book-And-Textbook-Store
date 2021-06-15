package com.service.store.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.store.dao.ListOfItemsRepository;
import com.service.store.dao.UserRepository;
import com.service.store.entity.*;
import com.service.store.recommendation.RecommendationEngine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentService {

    @Autowired
    private ListOfItemsRepository listOfItemsRepository;

    @Autowired
    private UserRepository userRepository;

    public String getToken() throws IOException, InterruptedException {
        var values = new HashMap<String, String>() {{
            put("grant_type", "client_credentials");
            put("client_id", "409761");
            put("client_secret", "f14549901cfb0337a4186a6302457c2c");
        }};

        var objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestToken = HttpRequest.newBuilder()
                .uri(URI.create("https://secure.snd.payu.com/pl/standard/user/oauth/authorize?grant_type=client_credentials&client_id=409761&client_secret=f14549901cfb0337a4186a6302457c2c"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(requestToken,
                HttpResponse.BodyHandlers.ofString());

        JSONObject jsonObject = new JSONObject(response.body());

        return jsonObject.getString("access_token");
    }

    public String makePayment(String token, User user, String continueUrl) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        double costOfBasket = 0.00;

        JSONArray products = new JSONArray();
        ListOfItems basket = null;
        for(ListOfItems l: user.getListsOfItems()) {
            if (l.isBasket()) {
                basket = l;
                RecommendationEngine.addOrderInfoToPreferences(basket, user);
                for (Item i : basket.getItems()) {
                    i.setVisible(false);
                    costOfBasket += i.getPrice();
                    products.put(new JSONObject()
                            .put("name", i.getName())
                            .put("unitPrice", String.valueOf((int)(i.getPrice() * 100)))
                            .put("quantity", "1"));


                }
            }
        }
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setDateOfLastModification(Timestamp.valueOf(LocalDateTime.now()));
        orderInfo.setCost(costOfBasket);
        orderInfo.setListOfItems(basket);
        orderInfo.setStateOfOrder(StateOfOrder.PAID);
        List<OrderInfo> orderInfosList = user.getOrderInfos();
        orderInfosList.add(orderInfo);

        ListOfItems newBasket = new ListOfItems();
        newBasket.setBasket(true);
        newBasket.setDateAdded(Timestamp.valueOf(LocalDateTime.now()));
        newBasket.setName("basket");

        basket.setBasket(false);
        user.getListsOfItems().add(newBasket);


        JSONObject jsonObject1 = new JSONObject()
                //.put("notifyUrl","http://"+InetAddress.getLoopbackAddress().getHostName()+":8080/stateOfOrder")
                .put("customerIp", "127.0.0.1")
                .put("merchantPosId", "409761")
                .put("description", "textbook_store")
                .put("currencyCode", "PLN")
                .put("totalAmount", String.valueOf((int) (orderInfo.getCost()*100)))
                .put("buyer", new JSONObject()
                        .put("language", "en"))
                .put("products", products);
        if(continueUrl != null){
            jsonObject1.put("continueUrl", continueUrl);
        }

        if(user.getFirstName() != null){
            jsonObject1.getJSONObject("buyer")
                    .put("firstName", user.getFirstName());
        }
        if(user.getLastName() != null){
            jsonObject1.getJSONObject("buyer")
                    .put("lastName", user.getLastName());
        }
        if(user.getEmail() != null){
            jsonObject1.getJSONObject("buyer")
                    .put("email",user.getEmail());
        }


        HttpRequest requestOrder = HttpRequest.newBuilder()
                .uri(URI.create("https://secure.snd.payu.com/api/v2_1/orders"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject1.toString()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response1 = client.send(requestOrder,
                HttpResponse.BodyHandlers.ofString());

        JSONObject jsonObject2 = new JSONObject(response1.body());
        Map<String, Object> map = jsonObject2.toMap();
        String redirectUri = map.get("redirectUri").toString();

        return redirectUri;

    }
    public String makePaymentTest(String token, String continueUrl) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        JSONObject jsonObject1 = new JSONObject()
                //.put("notifyUrl","https://your.eshop.com/notify")
                .put("customerIp","127.0.0.1")
                .put("merchantPosId","409761")
                .put("description","textbook_store")
                .put("currencyCode","PLN")
                .put("totalAmount","21000")
                .put("buyer",new JSONObject()
                        .put("email","test.user@gmail.com")
                        //.put("firstName","Filip")
                        //.put("lastName","Wgda")
                        .put("language","en"))
                .put("products", new JSONArray()
                        .put(new JSONObject()
                                .put("name","a")
                                .put("unitPrice","21000")
                                .put("quantity","1")));
        if(continueUrl != null){
            jsonObject1.put("continueUrl", continueUrl);
        }

        HttpRequest requestOrder = HttpRequest.newBuilder()
                .uri(URI.create("https://secure.snd.payu.com/api/v2_1/orders"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject1.toString()))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        HttpResponse<String> response1 = client.send(requestOrder,
                HttpResponse.BodyHandlers.ofString());


        JSONObject jsonObject2 = new JSONObject(response1.body());
        Map<String, Object> map = jsonObject2.toMap();
        String redirectUri = map.get("redirectUri").toString();

        return redirectUri;

    }
}
