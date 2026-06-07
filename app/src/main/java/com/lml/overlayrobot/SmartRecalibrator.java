package com.lml.overlayrobot;

import android.graphics.Rect;

import java.util.List;

public class SmartRecalibrator {

    public static ScreenNode findBestMatch(ActionStep oldStep, List<ScreenNode> currentNodes) {
        if (oldStep == null || currentNodes == null || currentNodes.isEmpty()) return null;

        ScreenNode best = null;
        float bestScore = Float.MAX_VALUE;

        for (ScreenNode node : currentNodes) {
            if (!node.clickable) continue;

            float score = 0;

            if (oldStep.targetText != null && node.text != null &&
                oldStep.targetText.equalsIgnoreCase(node.text)) {
                score -= 1000;
            }
            if (oldStep.targetDesc != null && node.contentDescription != null &&
                oldStep.targetDesc.equalsIgnoreCase(node.contentDescription)) {
                score -= 800;
            }
            if (oldStep.className != null && node.className != null &&
                oldStep.className.equals(node.className)) {
                score -= 300;
            }
            if (oldStep.packageName != null && node.packageName != null &&
                oldStep.packageName.equals(node.packageName)) {
                score -= 200;
            }

            float dx = node.centerX() - oldStep.x;
            float dy = node.centerY() - oldStep.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            score += dist * 0.8f;

            if (score < bestScore) {
                bestScore = score;
                best = node;
            }
        }
        return best;
    }
}