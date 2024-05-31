package com.example.app.Models.Checks;

import com.example.app.Utils.Listeners;

public class CheckInfo {

    private final Listeners.OnFilterChangeStateListener.TYPE TYPE;

    private final String info;

    public CheckInfo(Listeners.OnFilterChangeStateListener.TYPE TYPE, String info) {
        this.TYPE = TYPE;
        this.info = info;
    }

    public Listeners.OnFilterChangeStateListener.TYPE getTYPE() {
        return TYPE;
    }

    public String getInfo() {
        return info;
    }
}
