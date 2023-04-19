package com.lightweightapp.walletservice.model;

public class UserWalletCreationModel {
    private int userId;

    public UserWalletCreationModel() {
    }

    public UserWalletCreationModel(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
