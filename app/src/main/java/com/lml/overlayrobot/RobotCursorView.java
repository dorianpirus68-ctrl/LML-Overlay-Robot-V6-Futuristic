package com.lml.overlayrobot;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;

public class RobotCursorView extends View {
    private float targetX;
    private float targetY;
    private float currentX;
    private float currentY;
    private String label;
    private long animationStart;
    private Paint headPaint;
    private Paint visierePaint;
    private Paint eyePaint;
    private Paint brainPaint;
    private Paint borderPaint;
    private Paint energyPaint;

    public RobotCursorView(android.content.Context context) {
        super(context);
        init();
    }

    public RobotCursorView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        headPaint = new Paint();
        headPaint.setColor(0xFFFFFFFF);
        headPaint.setStyle(Paint.Style.FILL);

        visierePaint = new Paint();
        visierePaint.setColor(0xFF000000);
        visierePaint.setStyle(Paint.Style.FILL);

        eyePaint = new Paint();
        eyePaint.setColor(0xFF00FFFF);
        eyePaint.setStyle(Paint.Style.FILL);

        brainPaint = new Paint();
        brainPaint.setColor(0xFFFFD700);
        brainPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint();
        borderPaint.setColor(0xFF00FFFF);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        energyPaint = new Paint();
        energyPaint.setColor(0xFF9D00FF);
        energyPaint.setStyle(Paint.Style.STROKE);
        energyPaint.setStrokeWidth(1);

        currentX = 100;
        currentY = 100;
        targetX = 100;
        targetY = 100;
        label = "LML AGI";
    }

    public void moveTo(float x, float y, String label) {
        targetX = x;
        targetY = y;
        this.label = label;
        animationStart = System.currentTimeMillis();
        postInvalidateDelayed(33);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Animation de mouvement
        long elapsed = System.currentTimeMillis() - animationStart;
        float progress = Math.min(1.0f, elapsed / 300.0f);
        currentX = currentX + (targetX - currentX) * progress;
        currentY = currentY + (targetY - currentY) * progress;

        float size = 60;
        float headRadius = size / 2;

        // Halo holographique
        int haloColor = ThemePulse.color(System.currentTimeMillis());
        Paint haloPaint = new Paint();
        haloPaint.setColor(haloColor);
        haloPaint.setStyle(Paint.Style.STROKE);
        haloPaint.setStrokeWidth(2);
        haloPaint.setAlpha(100);
        canvas.drawCircle(currentX, currentY, headRadius + 15, haloPaint);
        canvas.drawCircle(currentX, currentY, headRadius + 25, haloPaint);

        // Tête robot
        canvas.drawCircle(currentX, currentY, headRadius, headPaint);

        // Visière (noir)
        canvas.drawRect(currentX - headRadius + 5, currentY - headRadius / 2,
                       currentX - 5, currentY + headRadius / 2, visierePaint);

        // Yeux (cyan néon)
        float eyeY = currentY - headRadius / 3;
        canvas.drawCircle(currentX + 8, eyeY - 5, 5, eyePaint);
        canvas.drawCircle(currentX + 8, eyeY + 5, 5, eyePaint);

        // Cerveau dans dôme transparent
        canvas.drawCircle(currentX, currentY + headRadius + 10, 8, brainPaint);
        canvas.drawCircle(currentX, currentY + headRadius + 10, 10, borderPaint);

        // Corps robot
        float bodyTop = currentY + headRadius + 5;
        canvas.drawRect(currentX - 12, bodyTop, currentX + 12, bodyTop + 35, headPaint);

        // Bras gauche
        canvas.drawLine(currentX - 12, bodyTop + 10, currentX - 30, bodyTop + 15, headPaint);
        canvas.drawCircle(currentX - 30, bodyTop + 15, 4, headPaint);

        // Bras droit
        canvas.drawLine(currentX + 12, bodyTop + 10, currentX + 30, bodyTop + 15, headPaint);
        canvas.drawCircle(currentX + 30, bodyTop + 15, 4, headPaint);

        // Anneaux d'énergie
        long time = System.currentTimeMillis();
        int ring1Alpha = (int) (150 + 100 * Math.sin(time / 500.0));
        int ring2Alpha = (int) (150 + 100 * Math.sin(time / 800.0));

        Paint ring1 = new Paint();
        ring1.setColor(0xFF00FFFF);
        ring1.setStyle(Paint.Style.STROKE);
        ring1.setStrokeWidth(1);
        ring1.setAlpha(ring1Alpha);
        canvas.drawCircle(currentX, currentY + 20, 50 + (int)(20 * Math.sin(time / 1000.0)), ring1);

        Paint ring2 = new Paint();
        ring2.setColor(0xFF9D00FF);
        ring2.setStyle(Paint.Style.STROKE);
        ring2.setStrokeWidth(1);
        ring2.setAlpha(ring2Alpha);
        canvas.drawCircle(currentX, currentY + 20, 70 + (int)(20 * Math.cos(time / 1200.0)), ring2);

        // Label
        Paint labelPaint = new Paint();
        labelPaint.setColor(0xFF00FFFF);
        labelPaint.setTextSize(12);
        labelPaint.setFakeBoldText(true);
        canvas.drawText("LML AGI", currentX - 20, currentY - headRadius - 15, labelPaint);

        if (progress < 1.0f) {
            postInvalidateDelayed(33);
        }
    }
}
