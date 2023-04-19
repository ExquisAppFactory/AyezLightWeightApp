package com.lightweightapp.walletservice.dbResource;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "user_wallet", schema = "light_weight_app")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "wallet_balance")
    private double walletBalance;

    @Column(name = "date_updated")
    private Timestamp dateUpdated;

    public Wallet()
    {

    }

    public Wallet(int userId, double walletBalance, Timestamp dateUpdated) {
        this.userId = userId;
        this.walletBalance = walletBalance;
        this.dateUpdated = dateUpdated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Timestamp getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Timestamp dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
