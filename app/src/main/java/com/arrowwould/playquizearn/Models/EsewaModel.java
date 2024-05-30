package com.arrowwould.playquizearn.Models;

public class EsewaModel {

    private String id,eSewaCard;

    public EsewaModel() {
    }

    public EsewaModel(String id, String eSewaCard) {
        this.id = id;
        this.eSewaCard = eSewaCard;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String geteSewaCard() {
        return eSewaCard;
    }

    public void seteSewaCard(String eSewaCard) {
        this.eSewaCard = eSewaCard;
    }
}
