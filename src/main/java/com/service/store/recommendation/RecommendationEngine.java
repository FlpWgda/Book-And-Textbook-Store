package com.service.store.recommendation;

import com.service.store.entity.Category;
import com.service.store.entity.Item;
import com.service.store.entity.ListOfItems;
import com.service.store.entity.User;
import org.apache.avro.generic.GenericData;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationEngine {

    public static Map<String,Long> genreIds = new HashMap<>();
    public static Map<String,Long> userIds = new HashMap<>();
    public static Map<Long, Map<Long,Integer>> userPreferences = new HashMap<>();

    public static List<RecommendedItem> recommendGenres(String userLogin, int howMany) throws TasteException {

        FastByIDMap<PreferenceArray> fastByIDMap = new FastByIDMap<>();
        for(Map.Entry<Long,Map<Long,Integer>> entry: userPreferences.entrySet()){
            List<Preference> preferenceList = new ArrayList<>();
            for(Map.Entry<Long,Integer> entry2: entry.getValue().entrySet()){
                preferenceList.add(new GenericPreference(entry.getKey(),entry2.getKey(),entry2.getValue()));
            }
            //System.out.println("Preference list: " + preferenceList.toString());
            //System.out.println("Genre ids: " + genreIds.toString());
            //System.out.println("User ids: " + userIds.toString());
            PreferenceArray genericUserPreferenceArray = new GenericUserPreferenceArray(preferenceList);
            fastByIDMap.put(entry.getKey(),genericUserPreferenceArray);
        }
        DataModel genericDataModel = new GenericDataModel(fastByIDMap);
        CityBlockSimilarity similarity = new CityBlockSimilarity(genericDataModel);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1,similarity, genericDataModel);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(genericDataModel, neighborhood, similarity);

        List<RecommendedItem> recommendations = recommender.recommend(userIds.get(userLogin), howMany);
        /**Map<String,Float> genreNamesList = new HashMap<>();
        for(RecommendedItem r: recommendations){
            for(Map.Entry<String,Long> entry: genreIds.entrySet()){
                if(r.getItemID() == entry.getValue()){
                    genreNamesList.put(entry.getKey(),r.getValue());
                }
            }
        }**/

        return recommendations;

    }
    public static String genreNameById(long id){
        for(Map.Entry<String,Long> entry: genreIds.entrySet()){
                if(id == entry.getValue()){
                    return entry.getKey();
                }
        }
        return "";
    }

    public static void addOrderInfoToPreferences(ListOfItems listOfItems, User user){
        long userId;
        if(RecommendationEngine.userIds.containsKey(user.getLogin())){
            userId = RecommendationEngine.userIds.get(user.getLogin());
        }
        else{
            userId = RecommendationEngine.userIds.size();
            RecommendationEngine.userIds.put(user.getLogin(),userId);
        }
        for (Item i : listOfItems.getItems()) {


            for(Category c:i.getCategories()){
                long genreId;
                if(RecommendationEngine.genreIds.containsKey(c.getGenreName())){
                    genreId = RecommendationEngine.genreIds.get(c.getGenreName());
                }
                else{
                    genreId = RecommendationEngine.genreIds.size();
                    RecommendationEngine.genreIds.put(c.getGenreName(),genreId);
                }

                if(RecommendationEngine.userPreferences.containsKey(userId)){
                    if(RecommendationEngine.userPreferences.get(userId).containsKey(genreId)){
                        RecommendationEngine.userPreferences.get(userId).put(genreId,RecommendationEngine.userPreferences.get(userId).get(genreId)+1);
                    }
                    else{
                        RecommendationEngine.userPreferences.get(userId).put(genreId,1);
                    }
                }
                else{
                    Map<Long,Integer> temp = new HashMap<>();
                    temp.put(genreId,1);
                    RecommendationEngine.userPreferences.put(userId, temp);
                }

            }

        }
    }
    public static void addUserToPreferenceList(User user){

        long userId = RecommendationEngine.userIds.size();
        RecommendationEngine.userIds.put(user.getLogin(),userId);

        Map<Long,Integer> userPrefs = new HashMap<>();

        /*for(Map.Entry<String,Long> entry: genreIds.entrySet()){
            userPrefs.put(entry.getValue(), 1);
        }*/
        RecommendationEngine.userPreferences.put(userId, userPrefs);

    }

}
