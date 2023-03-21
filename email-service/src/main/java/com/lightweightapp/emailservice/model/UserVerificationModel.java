package com.lightweightapp.emailservice.model;

public class UserVerificationModel {
    private int userId;
    private String email;

    public UserVerificationModel()
    {

    }

    public UserVerificationModel(int userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
