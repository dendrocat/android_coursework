package com.example.app.Models;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Comparator;

public class Feedback {

    private String UID;
    private float rate;
    private String feedback;
    private ArrayList<String> images;

    public Feedback() {
        images = new ArrayList<>();
    }


    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }



    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
