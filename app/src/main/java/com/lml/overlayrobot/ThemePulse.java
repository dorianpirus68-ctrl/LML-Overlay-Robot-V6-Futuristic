package com.lml.overlayrobot;

public class ThemePulse {
    private static final int[] COLORS = {
        0xFF00BCD4, // Cyan
        0xFFFF6B9D, // Pink
        0xFFFF6B00, // Gold
        0xFF00FF00, // Green
        0xFF9D00FF  // Violet
    };

    public static int color(long timeMs) {
        int index = (int) ((timeMs / 300) % COLORS.length);
        return COLORS[index];
    }

    public static int darkPanel() {
        return 0xFF0A0E27;
    }

    public static int darkPanelSemi() {
        return 0xCC0A0E27;
    }
}
