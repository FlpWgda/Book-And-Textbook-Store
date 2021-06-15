package com.service.store;

import com.service.store.entity.Category;
import com.service.store.recommendation.RecommendationEngine;
import com.service.store.security.JwtFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class StoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(StoreApplication.class, args);
        recommendationSetup();

    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new JwtFilter());
        filterRegistrationBean.setUrlPatterns(Collections.singleton("/api/*"));
        return filterRegistrationBean;
    }
    public static void recommendationSetup(){
        List<String> genreList = List.of(
                "Horror", //0
                "Fiction", //1
                "Thriller", //2
                "Nonfiction", //3
                "Psychology", //4
                "Self Help", //5
                "Biography", //6
                "Business", //7
                "Economics", //8
                "Fantasy", //9
                "Novel", //10
                "Magic Realism", //11
                "fantasy", //12
                "magic realism", //13
                "novel" ); //14

        for(int i=0; i<genreList.size(); i++){
            RecommendationEngine.genreIds.put(genreList.get(i), (long) i);
        }

        List<String> userList = List.of("User1","User2","User3","User4");

        for(int i=0; i<userList.size(); i++){
            RecommendationEngine.userIds.put(userList.get(i), (long) i);
        }
        Map<Long,Integer> user1Prefs = new HashMap<>();
        user1Prefs.put((long) 0,2);
        user1Prefs.put((long) 2,5);
        user1Prefs.put((long) 9,3);
        user1Prefs.put((long) 12,7);
        user1Prefs.put((long) 11,3);
        user1Prefs.put((long) 13,2);
        RecommendationEngine.userPreferences.put((long) 0, user1Prefs);

        Map<Long,Integer> user2Prefs = new HashMap<>();
        user2Prefs.put((long) 1,8);
        user2Prefs.put((long) 4,3);
        user2Prefs.put((long) 5,1);
        user2Prefs.put((long) 9,8);
        user2Prefs.put((long) 12,8);
        RecommendationEngine.userPreferences.put((long) 1, user2Prefs);

        Map<Long,Integer> user3Prefs = new HashMap<>();
        user3Prefs.put((long) 2,4);
        user3Prefs.put((long) 3,2);
        user3Prefs.put((long) 5,12);
        user3Prefs.put((long) 7,1);
        user3Prefs.put((long) 8,10);
        user3Prefs.put((long) 11,1);
        user3Prefs.put((long) 13,5);
        RecommendationEngine.userPreferences.put((long) 2, user3Prefs);

        Map<Long,Integer> user4Prefs = new HashMap<>();
        user4Prefs.put((long) 0,8);
        user4Prefs.put((long) 2,7);
        user4Prefs.put((long) 4,2);
        user4Prefs.put((long) 9,1);
        user4Prefs.put((long) 12,2);
        user4Prefs.put((long) 11,2);
        user4Prefs.put((long) 13,4);
        RecommendationEngine.userPreferences.put((long) 3, user4Prefs);


    }


}
