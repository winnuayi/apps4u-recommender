package com.ciheul.recommender;

public class RecommendedItemsRequest {

    String androidID;
    int numOfRecommendations;
    int timeContex;

    public RecommendedItemsRequest() {
    }

    public String getAndroidID() {
        return androidID;
    }

    public int getNumOfRecommendations() {
        return numOfRecommendations;
    }

    public int getTimeContext() {
        return timeContex;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public void setTimeContext(int timeContex) {
        this.timeContex = timeContex;
    }

    public void setNumOfRecommendations(int numOfRecommendations) {
        this.numOfRecommendations = numOfRecommendations;
    }

}