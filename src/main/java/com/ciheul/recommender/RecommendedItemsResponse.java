package com.ciheul.recommender;

import java.util.List;

public class RecommendedItemsResponse {

    int size;
    List<String> recommendedItems;
    String status;

    public RecommendedItemsResponse() {
    }

    public RecommendedItemsResponse(
            int size,
            List<String> recommendedItems,
            String status) {
        this.size = size;
        this.recommendedItems = recommendedItems;
        this.status = status;
    }
    
    // datatype is different. recommendeditem to string
//    public RecommendedItemsModel(List<RecommendedItem> recommendations) {
//        recommendedItems = new ArrayList<String>();
//        if (recommendations != null)
//            for (RecommendedItem recommendation : recommendations) {
//                recommendedItems.add(String.valueOf(recommendation.getItemID()));
//            }
//    }

    public List<String> getRecommendedItems() {
        return recommendedItems;
    }

    public void setRecommendedItems(List<String> recommendedItems) {
        this.recommendedItems = recommendedItems;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
