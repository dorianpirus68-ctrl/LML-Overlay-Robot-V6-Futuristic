package com.lml.overlayrobot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import java.util.List;

public class FloatingRobotService extends Service {

    private WindowManager windowManager;
    private LinearLayout floatingPanel;
    private RobotCursorView robotView;
    private ScreenMapOverlayView mapView;
    private ActionRecorder recorder;
    private SmartActionEngine engine;
    private BrainCore brain;
    private LmlAccessibilityService accessibilityService;

    private WindowManager.LayoutParams panelParams;
    private WindowManager.LayoutParams robotParams;
    private WindowManager.LayoutParams mapParams;

    private RobotMode currentMode = RobotMode.POINTAGE;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        recorder = new ActionRecorder(this);
        brain = new BrainCore();
        engine = new SmartActionEngine(this, recorder, brain);

        createNotificationChannel();
        startForeground(1, createNotification());

        createFloatingPanel();
        createRobotView();
        createMapView();

        accessibilityService = LmlAccessibilityService.getInstance();
        if (accessibilityService != null) {
            accessibilityService.setRecorder(recorder);
            accessibilityService.setMapView(mapView);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "lml_robot_channel", "LML Robot", NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, "lml_robot_channel")
                .setContentTitle("LML Overlay Robot V6")
                .setContentText("Futuristic robot is active")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build();
    }

    private void createFloatingPanel() {
        floatingPanel = new LinearLayout(this);
        floatingPanel.setOrientation(LinearLayout.VERTICAL);
        floatingPanel.setBackgroundColor(0xDD000000);
        floatingPanel.setPadding(12, 12, 12, 12);

        TextView handle = new TextView(this);
        handle.setText("LML ROBOT v6");
        handle.setTextColor(0xFF00E5FF);
        handle.setTextSize(14);
        handle.setGravity(Gravity.CENTER);
        floatingPanel.addView(handle);

        addButton("BRAIN", 0xFFAA00FF, v -> brain.showBrainUI(this));
        addButton("SCAN", 0xFF00E5FF, v -> performScan());
        addButton("REC", 0xFFFF0055, v -> startRec());
        addButton("STOP REC", 0xFF888888, v -> stopRec());
        addButton("PLAY POINTAGE", 0xFF00FF9F, v -> playPointage());
        addButton("PLAY SIMULATION", 0xFFFFFF00, v -> playSimulation());
        addButton("AUTO SAFE", 0xFF00AAFF, v -> setMode(RobotMode.AUTO_SAFE));
        addButton("ASSISTÉ", 0xFFFFAA00, v -> setMode(RobotMode.ASSISTED));
        addButton("VALIDER ACTION", 0xFF00FF00, v -> validateAction());
        addButton("PASSER", 0xFF888888, v -> {});
        addButton("CLEAR", 0xFFFF5555, v -> recorder.clear());
        addButton("STOP URGENCE", 0xFFFF0000, v -> stopUrgence());

        panelParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        panelParams.gravity = Gravity.TOP | Gravity.START;
        panelParams.x = 60;
        panelParams.y = 120;

        handle.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = panelParams.x;
                        initialY = panelParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        panelParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        panelParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingPanel, panelParams);
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(floatingPanel, panelParams);
    }

    private void addButton(String text, int color, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(color);
        btn.setTextSize(11);
        btn.setPadding(8, 4, 8, 4);
        btn.setOnClickListener(listener);
        floatingPanel.addView(btn);
    }

    private void createRobotView() {
        robotView = new RobotCursorView(this);
        robotParams = new WindowManager.LayoutParams(
                220, 220,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        robotParams.gravity = Gravity.TOP | Gravity.START;
        robotParams.x = 300;
        robotParams.y = 400;
        windowManager.addView(robotView, robotParams);
    }

    private void createMapView() {
        mapView = new ScreenMapOverlayView(this);
        mapParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager.addView(mapView, mapParams);
    }

    private void performScan() {
        if (accessibilityService != null) {
            List<ScreenNode> nodes = accessibilityService.scanCurrentScreen();
            if (nodes != null && mapView != null) {
                mapView.updateNodes(nodes);
            }
            brain.getSwarm().pulseAgent("Vision", "SCAN COMPLETE");
        }
    }

    private void startRec() {
        recorder.startRecording();
        if (robotView != null) robotView.setLabel("REC ON");
    }

    private void stopRec() {
        recorder.stopRecording();
        if (robotView != null) robotView.setLabel("REC OFF");
    }

    private void playPointage() {
        engine.setMode(RobotMode.POINTAGE);
        engine.executePointage(recorder.getSteps(), robotView);
    }

    private void playSimulation() {
        engine.setMode(RobotMode.SIMULATION);
        engine.executeSimulation(recorder.getSteps(), robotView, mapView);
    }

    private void setMode(RobotMode mode) {
        currentMode = mode;
        engine.setMode(mode);
        if (robotView != null) robotView.setLabel(mode.name());
    }

    private void validateAction() {
        if (currentMode == RobotMode.ASSISTED && accessibilityService != null) {
            List<ActionStep> steps = recorder.getSteps();
            if (!steps.isEmpty()) {
                ActionStep last = steps.get(steps.size() - 1);
                accessibilityService.performClick(last.x, last.y);
            }
        }
    }

    private void stopUrgence() {
        if (robotView != null) robotView.setLabel("STOPPED");
        if (mapView != null) mapView.updateNodes(null);
        recorder.stopRecording();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingPanel != null) windowManager.removeView(floatingPanel);
        if (robotView != null) windowManager.removeView(robotView);
        if (mapView != null) windowManager.removeView(mapView);
    }
}