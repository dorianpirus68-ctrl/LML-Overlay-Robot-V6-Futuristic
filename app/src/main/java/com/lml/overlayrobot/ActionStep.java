package com.lml.overlayrobot;

import android.graphics.Rect;

public class ActionStep {
    public int id;
    public String type = "CLICK";
    public float x;
    public float y;
    public String targetText;
    public String targetDesc;
    public String className;
    public String packageName;
    public Rect bounds;
    public long timestamp;

    public ActionStep() {
        this.timestamp = System.currentTimeMillis();
    }

    public ActionStep(float x, float y, String text, String desc, String cls, String pkg, Rect bounds) {
        this();
        this.x = x;
        this.y = y;
        this.targetText = text;
        this.targetDesc = desc;
        this.className = cls;
        this.packageName = pkg;
        this.bounds = bounds;
    }
}