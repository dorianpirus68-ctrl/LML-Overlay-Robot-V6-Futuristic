package com.lml.overlayrobot;

public class Agent {
    public String name;
    public String status = "IDLE";
    public int pulse = 0;

    public Agent(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
        this.pulse = 12;
    }
}