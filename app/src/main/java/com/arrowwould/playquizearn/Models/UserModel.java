package com.arrowwould.playquizearn.Models;

public class UserModel {

    private String profile,name,number,email,password,userId;
    private String referCode;
    private int coins;
    private int spins;
    //private String date;

    public UserModel() {
    }

    public UserModel(String profile, String name, String number, String email, String password, String userId, String referCode, int coins, int spins) {
        this.profile = profile;
        this.name = name;
        this.number = number;
        this.email = email;
        this.password = password;
        this.userId = userId;
        this.referCode = referCode;
        this.coins = coins;
        this.spins = spins;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getSpins() {
        return spins;
    }

    public void setSpins(int spins) {
        this.spins = spins;
    }
}
