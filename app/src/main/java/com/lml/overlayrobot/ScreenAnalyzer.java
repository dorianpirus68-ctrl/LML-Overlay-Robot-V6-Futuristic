package com.lml.overlayrobot;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class ScreenAnalyzer {

    private final List<ScreenNode> nodes = new ArrayList<>();
    private int counter = 0;

    public List<ScreenNode> analyze(AccessibilityNodeInfo root) {
        nodes.clear();
        counter = 0;
        if (root != null) {
            traverse(root);
        }
        return new ArrayList<>(nodes);
    }

    private void traverse(AccessibilityNodeInfo node) {
        if (node == null) return;

        ScreenNode sn = new ScreenNode(node, counter++);
        if (sn.isRelevant() || sn.clickable) {
            nodes.add(sn);
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            traverse(child);
            if (child != null) child.recycle();
        }
    }

    public String buildReport() {
        return "Screen nodes: " + nodes.size() + " | Clickable: " + countClickable();
    }

    private int countClickable() {
        int c = 0;
        for (ScreenNode n : nodes) if (n.clickable) c++;
        return c;
    }
}