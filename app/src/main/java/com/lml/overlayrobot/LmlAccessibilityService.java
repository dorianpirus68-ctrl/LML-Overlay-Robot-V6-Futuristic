package com.lml.overlayrobot;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class LmlAccessibilityService extends AccessibilityService {

    private static LmlAccessibilityService instance;
    private final ScreenAnalyzer analyzer = new ScreenAnalyzer();
    private ScreenMapOverlayView mapView;
    private ActionRecorder recorder;

    public static LmlAccessibilityService getInstance() {
        return instance;
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    public void setMapView(ScreenMapOverlayView view) {
        this.mapView = view;
    }

    public void setRecorder(ActionRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED && recorder != null) {
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                float x = event.getX();
                float y = event.getY();
                String text = source.getText() != null ? source.getText().toString() : null;
                String desc = source.getContentDescription() != null ? source.getContentDescription().toString() : null;
                String cls = source.getClassName() != null ? source.getClassName().toString() : null;
                String pkg = source.getPackageName() != null ? source.getPackageName().toString() : null;
                recorder.recordClick(x, y, text, desc, cls, pkg, null);
                source.recycle();
            }
        }

        if (mapView != null && (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED)) {
            AccessibilityNodeInfo root = getRootInActiveWindow();
            if (root != null) {
                List<ScreenNode> nodes = analyzer.analyze(root);
                mapView.updateNodes(nodes);
                root.recycle();
            }
        }
    }

    public List<ScreenNode> scanCurrentScreen() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return null;
        List<ScreenNode> nodes = analyzer.analyze(root);
        root.recycle();
        return nodes;
    }

    public void performClick(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, 60);
        GestureDescription gesture = new GestureDescription.Builder().addStroke(stroke).build();
        dispatchGesture(gesture, null, null);
    }

    @Override
    public void onInterrupt() {
    }
}