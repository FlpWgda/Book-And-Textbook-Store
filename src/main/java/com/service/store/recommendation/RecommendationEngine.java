package com.service.store.recommendation;

import com.service.store.entity.Item;
import org.apache.avro.generic.GenericData;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;

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
            PreferenceArray genericItemPreferenceArray = new GenericItemPreferenceArray(preferenceList);
            fastByIDMap.put(entry.getKey(),genericItemPreferenceArray);
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

}
