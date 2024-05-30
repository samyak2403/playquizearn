package com.arrowwould.playquizearn.Adapters;

public class TrHistoryModel {

    String amount,number;
    String paymentMethode;
    String status;
    String coin;
    String date;


    public TrHistoryModel() {
    }


    public TrHistoryModel(String amount, String number, String paymentMethode, String status, String coin, String date) {
        this.amount = amount;
        this.number = number;
        this.paymentMethode = paymentMethode;
        this.status = status;
        this.coin = coin;
        this.date = date;
    }


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPaymentMethode() {
        return paymentMethode;
    }

    public void setPaymentMethode(String paymentMethode) {
        this.paymentMethode = paymentMethode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
