package com.lml.overlayrobot;

import android.graphics.Color;

public class ThemePulse {

    private static float hue = 180f;

    public static int getNeonColor() {
        hue = (hue + 1.2f) % 360f;
        float[] hsv = {hue, 1f, 1f};
        return Color.HSVToColor(hsv);
    }

    public static int getCyan() { return 0xFF00E5FF; }
    public static int getMagenta() { return 0xFFFF00AA; }
    public static int getPurple() { return 0xFFAA00FF; }
    public static int getLime() { return 0xFF00FF9F; }

    public static int pulseAlpha(int baseColor, float factor) {
        int alpha = (int) (255 * (0.6f + 0.4f * factor));
        return (baseColor & 0x00FFFFFF) | (alpha << 24);
    }
}