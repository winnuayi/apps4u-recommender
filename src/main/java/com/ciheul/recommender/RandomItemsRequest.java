package com.ciheul.recommender;

public class RandomItemsRequest {

    String androidID;
    int numOfRecommendations;
    int page;
    String minRatingValue;
    String maxRatingValue;
    int minRatingCount;
    int maxRatingCount;
    int minInstalls;
    String category;

    public RandomItemsRequest() {
    }

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public int getNumOfRecommendations() {
        return numOfRecommendations;
    }

    public void setNumOfRecommendations(int numOfRecommendations) {
        this.numOfRecommendations = numOfRecommendations;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getMinRatingValue() {
        return minRatingValue;
    }

    public void setMinRatingValue(String minRatingValue) {
        this.minRatingValue = minRatingValue;
    }

    public String getMaxRatingValue() {
        return maxRatingValue;
    }

    public void setMaxRatingValue(String maxRatingValue) {
        this.maxRatingValue = maxRatingValue;
    }

    public int getMinRatingCount() {
        return minRatingCount;
    }

    public void setMinRatingCount(int minRatingCount) {
        this.minRatingCount = minRatingCount;
    }

    public int getMaxRatingCount() {
        return maxRatingCount;
    }

    public void setMaxRatingCount(int maxRatingCount) {
        this.maxRatingCount = maxRatingCount;
    }

    public int getMinInstalls() {
        return minInstalls;
    }

    public void setMinInstalls(int minInstalls) {
        this.minInstalls = minInstalls;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
