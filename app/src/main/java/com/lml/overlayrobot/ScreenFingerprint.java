package com.lml.overlayrobot;

import java.util.List;

public class ScreenFingerprint {

    public static String createFingerprint(List<ScreenNode> nodes) {
        StringBuilder sb = new StringBuilder();
        for (ScreenNode n : nodes) {
            if (n.clickable && n.text != null) {
                sb.append(n.text).append("|");
            }
        }
        return sb.toString();
    }
}