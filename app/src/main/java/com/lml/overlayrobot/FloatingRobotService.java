package com.lml.overlayrobot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.app.NotificationCompat;

public class FloatingRobotService extends Service {
    private WindowManager windowManager;
    private View panelView;
    private RobotCursorView robotView;
    private ScreenMapOverlayView mapView;
    private SmartActionEngine actionEngine;
    private BrainCore brainCore;
    private int currentMode = RobotMode.POINT_ONLY;
    private boolean isRecording = false;
    private int modeIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, new NotificationCompat.Builder(this, "robot_channel")
                .setContentTitle("LML Robot")
                .setContentText("Running...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build());

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        brainCore = new BrainCore();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createFloatingPanel();
        createRobotViews();
        connectAccessibilityService();
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("robot_channel",
                    "LML Robot", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void createFloatingPanel() {
        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        panelView = createPanelContent();
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                480, 800,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(panelView, params);
        makePanelDraggable();
    }

    private View createPanelContent() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackgroundColor(0xFF0A0E27);
        panel.setPadding(10, 10, 10, 10);

        // Title
        TextView titleView = new TextView(this);
        titleView.setText("LML ROBOT");
        titleView.setTextColor(0xFF00BCD4);
        titleView.setTextSize(14);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        panel.addView(titleView);

        // Status TextView
        TextView statusView = new TextView(this);
        statusView.setText("Ready");
        statusView.setTextColor(0xFFCCCCCC);
        statusView.setTextSize(10);
        statusView.setPadding(0, 5, 0, 10);
        panel.addView(statusView);

        // ScrollView for buttons
        ScrollView scroll = new ScrollView(this);
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);

        // SCAN button
        Button btnScan = createButton("SCAN", 0xFF00BCD4);
        btnScan.setOnClickListener(v -> {
            scanScreen();
            updateStatus(statusView, "Scan completed");
        });
        buttonLayout.addView(btnScan);

        // REC button
        Button btnRec = createButton("REC", 0xFFFF0000);
        btnRec.setOnClickListener(v -> {
            startRecording();
            updateStatus(statusView, "Recording...");
        });
        buttonLayout.addView(btnRec);

        // STOP REC button
        Button btnStopRec = createButton("STOP REC", 0xFFFFAA00);
        btnStopRec.setOnClickListener(v -> {
            stopRecording();
            updateStatus(statusView, "Recording stopped");
        });
        buttonLayout.addView(btnStopRec);

        // PLAY POINTAGE button
        Button btnPlayPoint = createButton("PLAY POINTAGE", 0xFF00FF00);
        btnPlayPoint.setOnClickListener(v -> {
            playPointOnly();
            updateStatus(statusView, "Playing (point only)");
        });
        buttonLayout.addView(btnPlayPoint);

        // PLAY SIMULATION button
        Button btnPlaySim = createButton("PLAY SIMULATION", 0xFFFFFF00);
        btnPlaySim.setOnClickListener(v -> {
            playSimulation();
            updateStatus(statusView, "Playing (simulation)");
        });
        buttonLayout.addView(btnPlaySim);

        // AUTO SAFE button
        Button btnAutoSafe = createButton("AUTO SAFE", 0xFF00FFFF);
        btnAutoSafe.setOnClickListener(v -> {
            playAutoSafe();
            updateStatus(statusView, "Playing (auto safe)");
        });
        buttonLayout.addView(btnAutoSafe);

        // ASSISTÉ button
        Button btnAssisted = createButton("ASSISTÉ", 0xFFFF00FF);
        btnAssisted.setOnClickListener(v -> {
            playAssisted();
            updateStatus(statusView, "Playing (assisted)");
        });
        buttonLayout.addView(btnAssisted);

        // BRAIN button
        Button btnBrain = createButton("BRAIN", 0xFFFF6B00);
        btnBrain.setOnClickListener(v -> {
            showBrainStatus(statusView);
        });
        buttonLayout.addView(btnBrain);

        // CLEAR button
        Button btnClear = createButton("CLEAR", 0xFF888888);
        btnClear.setOnClickListener(v -> {
            clearRecording();
            updateStatus(statusView, "Cleared");
        });
        buttonLayout.addView(btnClear);

        // STOP URGENCE button
        Button btnEmergency = createButton("STOP URGENCE", 0xFFFF0000);
        btnEmergency.setOnClickListener(v -> {
            emergencyStop();
            updateStatus(statusView, "STOPPED");
        });
        buttonLayout.addView(btnEmergency);

        // EXIT button
        Button btnExit = createButton("EXIT", 0xFF666666);
        btnExit.setOnClickListener(v -> stopSelf());
        buttonLayout.addView(btnExit);

        scroll.addView(buttonLayout);
        panel.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        return panel;
    }

    private Button createButton(String text, int color) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setBackgroundColor(color);
        btn.setTextColor(0xFF000000);
        btn.setTextSize(10);
        btn.setPadding(5, 5, 5, 5);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 3, 0, 3);
        btn.setLayoutParams(params);
        return btn;
    }

    private void updateStatus(TextView tv, String text) {
        tv.setText(text);
    }

    private void createRobotViews() {
        robotView = new RobotCursorView(this);
        mapView = new ScreenMapOverlayView(this);

        WindowManager.LayoutParams robotParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        windowManager.addView(robotView, robotParams);
        windowManager.addView(mapView, robotParams);
    }

    private void makePanelDraggable() {
        float[] lastPos = {0, 0};
        panelView.setOnTouchListener((v, event) -> {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) panelView.getLayoutParams();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastPos[0] = event.getRawX();
                    lastPos[1] = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    params.x += (int)(event.getRawX() - lastPos[0]);
                    params.y += (int)(event.getRawY() - lastPos[1]);
                    windowManager.updateViewLayout(panelView, params);
                    lastPos[0] = event.getRawX();
                    lastPos[1] = event.getRawY();
                    break;
            }
            return true;
        });
    }

    private void connectAccessibilityService() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            actionEngine = new SmartActionEngine(service, robotView);
            service.setActionEngine(actionEngine);
            actionEngine.setCallback(message -> {
                // Update UI with message
            });
        }
    }

    private void scanScreen() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            service.scanNow();
            mapView.setNodes(service.getLastNodes());
        }
    }

    private void startRecording() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            service.startRecording();
            isRecording = true;
        }
    }

    private void stopRecording() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            service.stopRecording();
            isRecording = false;
        }
    }

    private void clearRecording() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            service.clearRecording();
        }
    }

    private void playPointOnly() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            service.playPointOnly();
        }
    }

    private void playSimulation() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            service.playSimulation();
        }
    }

    private void playAutoSafe() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            service.playAutoSafe();
        }
    }

    private void playAssisted() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            service.playAssisted();
        }
    }

    private void emergencyStop() {
        LmlAccessibilityService service = LmlAccessibilityService.INSTANCE;
        if (service != null) {
            service.emergencyStop();
        }
    }

    private void showBrainStatus(TextView statusView) {
        String status = brainCore.getStatus();
        statusView.setText(status);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (panelView != null) windowManager.removeView(panelView);
        if (robotView != null) windowManager.removeView(robotView);
        if (mapView != null) windowManager.removeView(mapView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
