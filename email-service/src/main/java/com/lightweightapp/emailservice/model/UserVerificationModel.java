package com.lightweightapp.emailservice.model;

public class UserVerificationModel {
    private int userId;
    private String email;

    private String firstName;

    private String token;

    public UserVerificationModel()
    {

    }

    public UserVerificationModel(int userId, String email, String firstName, String token) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.token = token;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
