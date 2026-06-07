package com.lml.overlayrobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RobotCursorView extends View {

    private final Paint headPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint visorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint eyePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint brainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint armPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String currentLabel = "READY";
    private float centerX = 100;
    private float centerY = 100;

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private int colorPhase = 0;

    private static class Particle {
        float x, y, vx, vy, life;
        Particle(float x, float y) {
            this.x = x; this.y = y;
            this.vx = (float) (Math.random() * 4 - 2);
            this.vy = (float) (Math.random() * 4 - 2);
            this.life = 30 + random.nextInt(40);
        }
        void update() {
            x += vx; y += vy;
            life -= 1.2f;
            vx *= 0.96f; vy *= 0.96f;
        }
    }

    public RobotCursorView(Context context) {
        super(context);
        setBackgroundColor(Color.TRANSPARENT);

        headPaint.setColor(0xFFFFFFFF);
        headPaint.setStyle(Paint.Style.FILL);
        visorPaint.setColor(0xFF111111);
        eyePaint.setColor(0xFF00FFFF);
        eyePaint.setStyle(Paint.Style.FILL);
        brainPaint.setColor(0xFFAA00FF);
        bodyPaint.setColor(0xFF222233);
        armPaint.setColor(0xFF00E5FF);
        armPaint.setStrokeWidth(8f);
        armPaint.setStyle(Paint.Style.STROKE);
        haloPaint.setStyle(Paint.Style.STROKE);
        haloPaint.setStrokeWidth(3f);
        labelPaint.setColor(Color.WHITE);
        labelPaint.setTextSize(32f);
        labelPaint.setShadowLayer(8f, 0, Color.BLACK);

        for (int i = 0; i < 12; i++) {
            particles.add(new Particle(0, 0));
        }

        startAnimation();
    }

    private void startAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateParticles();
                colorPhase = (colorPhase + 1) % 360;
                invalidate();
                handler.postDelayed(this, 16);
            }
        }, 16);
    }

    private void updateParticles() {
        for (Particle p : particles) {
            p.update();
            if (p.life <= 0) {
                p.x = centerX + random.nextInt(60) - 30;
                p.y = centerY + random.nextInt(60) - 30;
                p.life = 25 + random.nextInt(35);
            }
        }
    }

    public void moveTo(float x, float y, String label) {
        this.centerX = x;
        this.centerY = y;
        if (label != null) this.currentLabel = label;
        invalidate();
    }

    public void setLabel(String label) {
        if (label != null) this.currentLabel = label;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = centerX;
        float cy = centerY;
        int neon = ThemePulse.getNeonColor();

        // Halo
        haloPaint.setColor(neon);
        haloPaint.setAlpha(90);
        canvas.drawCircle(cx, cy - 10, 95, haloPaint);
        haloPaint.setAlpha(160);
        canvas.drawCircle(cx, cy - 10, 75, haloPaint);

        // Body
        RectF body = new RectF(cx - 38, cy + 25, cx + 38, cy + 95);
        canvas.drawRoundRect(body, 18, 18, bodyPaint);

        // Arms
        canvas.drawLine(cx - 38, cy + 45, cx - 75, cy + 25, armPaint);
        canvas.drawLine(cx + 38, cy + 45, cx + 75, cy + 25, armPaint);

        // Head
        canvas.drawCircle(cx, cy - 5, 48, headPaint);

        // Visière
        RectF visor = new RectF(cx - 32, cy - 25, cx + 32, cy + 8);
        canvas.drawRoundRect(visor, 12, 12, visorPaint);

        // Eyes (neon)
        eyePaint.setColor(neon);
        canvas.drawCircle(cx - 16, cy - 8, 7, eyePaint);
        canvas.drawCircle(cx + 16, cy - 8, 7, eyePaint);

        // Brain glow
        brainPaint.setColor(0xFFAA00FF);
        canvas.drawCircle(cx, cy - 35, 11, brainPaint);
        brainPaint.setColor(0xFFFF00FF);
        canvas.drawCircle(cx, cy - 35, 6, brainPaint);

        // Dome
        Paint dome = new Paint(Paint.ANTI_ALIAS_FLAG);
        dome.setColor(0x33FFFFFF);
        dome.setStyle(Paint.Style.FILL);
        canvas.drawArc(new RectF(cx - 42, cy - 55, cx + 42, cy - 5), 200, 140, true, dome);

        // Particles
        particlePaint.setColor(neon);
        for (Particle p : particles) {
            if (p.life > 0) {
                particlePaint.setAlpha((int) (p.life * 6));
                canvas.drawCircle(p.x, p.y, 3.5f, particlePaint);
            }
        }

        // Label
        if (currentLabel != null) {
            canvas.drawText(currentLabel, cx - currentLabel.length() * 9, cy - 75, labelPaint);
        }
    }
}