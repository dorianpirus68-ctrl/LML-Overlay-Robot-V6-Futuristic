package com.lml.overlayrobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ScreenMapOverlayView extends View {

    private final List<ScreenNode> nodes = new ArrayList<>();
    private final Paint neonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ScreenMapOverlayView(Context context) {
        super(context);
        setBackgroundColor(Color.TRANSPARENT);

        neonPaint.setStyle(Paint.Style.STROKE);
        neonPaint.setStrokeWidth(4f);
        neonPaint.setColor(0xFF00E5FF);
        neonPaint.setShadowLayer(12f, 0, 0, 0xFF00E5FF);

        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setStrokeWidth(5f);
        redPaint.setColor(0xFFFF0055);
        redPaint.setShadowLayer(14f, 0, 0, 0xFFFF0055);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(28f);
        textPaint.setShadowLayer(6f, 0, Color.BLACK);

        numberPaint.setColor(0xFFFFFF00);
        numberPaint.setTextSize(36f);
        numberPaint.setFakeBoldText(true);
    }

    public void updateNodes(List<ScreenNode> newNodes) {
        nodes.clear();
        if (newNodes != null) nodes.addAll(newNodes);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int number = 1;
        for (ScreenNode node : nodes) {
            RectF rect = new RectF(node.bounds);
            boolean sensitive = SafetyGuard.isSensitive(node.text, node.contentDescription);

            Paint paint = sensitive ? redPaint : neonPaint;
            canvas.drawRoundRect(rect, 12f, 12f, paint);

            if (node.clickable) {
                float cx = node.centerX();
                float cy = node.centerY();
                canvas.drawText(String.valueOf(number), cx - 18, cy + 12, numberPaint);
                number++;
            }
        }
    }
}