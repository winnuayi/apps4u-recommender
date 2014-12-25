package com.ciheul.recommender;

import java.util.List;
import java.util.Map;

public class SurpriseItemsResponse {
    int size;
    List<Map<String, Object>> recommendedItems;
    String status;

    public SurpriseItemsResponse(
            int size,
            List<Map<String, Object>> recommendedItems,
            String status) {
        this.size = size;
        this.recommendedItems = recommendedItems;
        this.status = status;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Map<String, Object>> getRecommendedItems() {
        return recommendedItems;
    }

    public void setRecommendedItems(
            List<Map<String, Object>> recommendedItems) {
        this.recommendedItems = recommendedItems;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
