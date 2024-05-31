package com.example.app.Models;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public class User {

    private String UID;

    private String name;

    private String image;

    private Uri tempUserImage;


    private final boolean seller;

    public boolean isSeller() {
        return seller;
    }

    public User(String name, boolean seller) {
        this.name = name;
        this.seller = seller;
        image = null;
        tempUserImage = null;
    }

    public void setTempUserImage(Uri image) { tempUserImage = image; }
    public Uri getTempUserImage() { return tempUserImage; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
