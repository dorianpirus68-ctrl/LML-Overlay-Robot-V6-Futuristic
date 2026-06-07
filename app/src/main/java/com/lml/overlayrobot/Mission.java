package com.lml.overlayrobot;

public class Mission {
    public String name = "Standby";
    public int progress = 0;

    public void setMission(String name) {
        this.name = name;
        this.progress = 0;
    }
}