package com.ciheul.recommender;

public class UserRegistrationRequest {
    
    String androidID;
    int timeOffset;
    
    public String getAndroidID() {
        return androidID;
    }
    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public int getTimeOffset() {
        return timeOffset;
    }
    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
    } 
}
