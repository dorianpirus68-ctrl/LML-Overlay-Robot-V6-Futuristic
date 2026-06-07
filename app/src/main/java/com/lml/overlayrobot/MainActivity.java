package com.lml.overlayrobot;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int OVERLAY_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0A0A0F);
        root.setPadding(60, 120, 60, 60);

        TextView title = new TextView(this);
        title.setText("LML OVERLAY ROBOT V6");
        title.setTextSize(28);
        title.setTextColor(0xFF00E5FF);
        title.setGravity(android.view.Gravity.CENTER);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Futuristic Holographic Automation");
        subtitle.setTextSize(16);
        subtitle.setTextColor(0xFF888888);
        subtitle.setGravity(android.view.Gravity.CENTER);
        root.addView(subtitle);

        addButton(root, "AUTORISER OVERLAY", 0xFF00E5FF, v -> requestOverlayPermission());
        addButton(root, "OUVRIR ACCESSIBILITÉ", 0xFFAA00FF, v -> openAccessibilitySettings());
        addButton(root, "LANCER ROBOT FLOTTANT", 0xFF00FF9F, v -> launchRobot());

        setContentView(root);
    }

    private void addButton(LinearLayout parent, String text, int color, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(0xFFFFFFFF);
        btn.setBackgroundColor(color);
        btn.setTextSize(16);
        btn.setPadding(40, 30, 40, 30);
        btn.setOnClickListener(listener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 30, 0, 0);
        parent.addView(btn, lp);
    }

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_REQUEST);
            }
        }
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    private void launchRobot() {
        startService(new Intent(this, FloatingRobotService.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_REQUEST) {
            // Permission result handled by system
        }
    }
}