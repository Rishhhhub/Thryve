package com.example.thryve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Mountain / area chart showing hourly heart-rate throughout the day.
 * Matches the orange area chart in the Strain Detail Figma design.
 */
public class HrTimelineView extends View {

    private Paint paintLine;
    private Paint paintFill;
    private int[] data;
    private int peakValue;

    public HrTimelineView(Context ctx) { super(ctx); init(); }
    public HrTimelineView(Context ctx, AttributeSet a) { super(ctx, a); init(); }
    public HrTimelineView(Context ctx, AttributeSet a, int d) { super(ctx, a, d); init(); }

    private void init() {
        paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(3f);
        paintLine.setColor(0xFFFF9800);

        paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill.setStyle(Paint.Style.FILL);
        // gradient set in onDraw where we have height
    }

    public void setData(int[] hourlyValues, int peak) {
        data = hourlyValues;
        peakValue = peak;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.length < 2) return;

        float w = getWidth();
        float h = getHeight();
        int   n = data.length;

        // Gradient fill
        paintFill.setShader(new LinearGradient(
                0, 0, 0, h,
                0xAAFF9800, 0x00FF9800,
                Shader.TileMode.CLAMP));

        // Find min/max
        int max = 0, min = Integer.MAX_VALUE;
        for (int v : data) { if (v > max) max = v; if (v < min) min = v; }
        if (max == min) max = min + 1;
        float range = max - min;

        float stepX = w / (n - 1);

        Path fillPath = new Path();
        Path linePath = new Path();

        for (int i = 0; i < n; i++) {
            float x = i * stepX;
            float y = h - ((data[i] - min) / range) * h * 0.85f - h * 0.05f;

            if (i == 0) {
                fillPath.moveTo(x, h);
                fillPath.lineTo(x, y);
                linePath.moveTo(x, y);
            } else {
                // Smooth cubic curve
                float prevX = (i - 1) * stepX;
                float prevY = h - ((data[i - 1] - min) / range) * h * 0.85f - h * 0.05f;
                float cpX   = (prevX + x) / 2f;
                fillPath.cubicTo(cpX, prevY, cpX, y, x, y);
                linePath.cubicTo(cpX, prevY, cpX, y, x, y);
            }
        }

        // Close fill path
        fillPath.lineTo(w, h);
        fillPath.close();

        canvas.drawPath(fillPath, paintFill);
        canvas.drawPath(linePath, paintLine);
    }
}
