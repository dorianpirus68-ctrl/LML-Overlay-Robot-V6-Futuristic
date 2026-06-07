package com.lml.overlayrobot;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

public class ScreenNode {
    public Rect bounds = new Rect();
    public String text;
    public String contentDescription;
    public String className;
    public String packageName;
    public boolean clickable;
    public boolean focusable;
    public int id;

    public ScreenNode(AccessibilityNodeInfo node, int id) {
        this.id = id;
        if (node.getBoundsInScreen(bounds)) {
            // ok
        }
        this.text = node.getText() != null ? node.getText().toString() : null;
        this.contentDescription = node.getContentDescription() != null ? node.getContentDescription().toString() : null;
        this.className = node.getClassName() != null ? node.getClassName().toString() : null;
        this.packageName = node.getPackageName() != null ? node.getPackageName().toString() : null;
        this.clickable = node.isClickable();
        this.focusable = node.isFocusable();
    }

    public boolean isRelevant() {
        return clickable || (text != null && !text.isEmpty()) || (contentDescription != null && !contentDescription.isEmpty());
    }

    public float centerX() {
        return bounds.centerX();
    }

    public float centerY() {
        return bounds.centerY();
    }
}