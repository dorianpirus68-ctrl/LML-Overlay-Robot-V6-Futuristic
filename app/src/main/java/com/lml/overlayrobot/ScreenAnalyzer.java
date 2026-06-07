package com.lml.overlayrobot;

import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.List;

public class ScreenAnalyzer {
    public List<ScreenNode> nodes = new ArrayList<>();

    public void analyze(AccessibilityNodeInfo root) {
        nodes.clear();
        if (root == null) return;
        traverse(root);
    }

    private void traverse(AccessibilityNodeInfo node) {
        if (node == null) return;

        String text = (node.getText() != null) ? node.getText().toString() : "";
        String desc = (node.getContentDescription() != null) ? node.getContentDescription().toString() : "";
        String className = (node.getClassName() != null) ? node.getClassName().toString() : "";
        String packageName = (node.getPackageName() != null) ? node.getPackageName().toString() : "";

        if (node.isClickable() || node.isFocusable() || !text.isEmpty() || !desc.isEmpty()) {
            android.graphics.Rect bounds = new android.graphics.Rect();
            node.getBoundsInScreen(bounds);

            boolean sensitive = SafetyGuard.canAutoExecute(new ActionStep(
                    packageName, className, text, desc, bounds, 0, 0, false, false)) == false;

            nodes.add(new ScreenNode(text, desc, className, packageName, bounds,
                    node.isClickable(), node.isFocused(), sensitive));
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            traverse(node.getChild(i));
        }
    }

    public ScreenNode findBestByText(String query) {
        String q = query.toLowerCase();
        for (ScreenNode n : nodes) {
            if (n.label().toLowerCase().contains(q)) {
                return n;
            }
        }
        return null;
    }

    public String buildReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Screen Report:\n");
        sb.append("Total elements: ").append(nodes.size()).append("\n");
        int clickableCount = 0;
        int sensitiveCount = 0;
        for (ScreenNode n : nodes) {
            if (n.clickable) clickableCount++;
            if (n.sensitive) sensitiveCount++;
        }
        sb.append("Clickable: ").append(clickableCount).append("\n");
        sb.append("Sensitive: ").append(sensitiveCount).append("\n");
        return sb.toString();
    }
}
