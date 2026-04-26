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
 * Smooth wave chart for HRV (Heart Rate Variability) over time.
 * Matches the green sinusoidal chart in the Recovery Detail Figma design.
 */
public class HrvChartView extends View {

    private Paint paintLine;
    private Paint paintFill;
    private float[] data;

    public HrvChartView(Context ctx) { super(ctx); init(); }
    public HrvChartView(Context ctx, AttributeSet a) { super(ctx, a); init(); }
    public HrvChartView(Context ctx, AttributeSet a, int d) { super(ctx, a, d); init(); }

    private void init() {
        paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(3f);
        paintLine.setColor(0xFF4CAF50);

        paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill.setStyle(Paint.Style.FILL);
    }

    public void setData(float[] values) {
        data = values;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.length < 2) return;

        float w = getWidth();
        float h = getHeight();
        int   n = data.length;

        // Vertical grid lines (subtle)
        Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(0x1A4CAF50);
        gridPaint.setStrokeWidth(1f);
        int gridCols = 4;
        for (int g = 0; g <= gridCols; g++) {
            float x = g * (w / gridCols);
            canvas.drawLine(x, 0, x, h, gridPaint);
        }

        // Gradient fill
        paintFill.setShader(new LinearGradient(
                0, 0, 0, h,
                0x554CAF50, 0x004CAF50,
                Shader.TileMode.CLAMP));

        float max = -Float.MAX_VALUE, min = Float.MAX_VALUE;
        for (float v : data) { if (v > max) max = v; if (v < min) min = v; }
        if (max == min) max = min + 1;
        float range = max - min;

        float stepX = w / (n - 1);

        Path fillPath = new Path();
        Path linePath = new Path();

        for (int i = 0; i < n; i++) {
            float x = i * stepX;
            float y = h - ((data[i] - min) / range) * h * 0.8f - h * 0.1f;

            if (i == 0) {
                fillPath.moveTo(x, h);
                fillPath.lineTo(x, y);
                linePath.moveTo(x, y);
            } else {
                float prevX = (i - 1) * stepX;
                float prevY = h - ((data[i - 1] - min) / range) * h * 0.8f - h * 0.1f;
                float cpX   = (prevX + x) / 2f;
                fillPath.cubicTo(cpX, prevY, cpX, y, x, y);
                linePath.cubicTo(cpX, prevY, cpX, y, x, y);
            }
        }

        fillPath.lineTo(w, h);
        fillPath.close();

        canvas.drawPath(fillPath, paintFill);
        canvas.drawPath(linePath, paintLine);
    }
}
