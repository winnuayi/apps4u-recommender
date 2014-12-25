package com.ciheul.recommender;

public class UserRegistrationResponse {
    String status;

    public UserRegistrationResponse(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
