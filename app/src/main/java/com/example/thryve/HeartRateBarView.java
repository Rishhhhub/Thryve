package com.example.thryve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Simple bar chart showing recent heart-rate readings.
 * Active bar (current HR) is highlighted bright red.
 */
public class HeartRateBarView extends View {

    private Paint paintBar;
    private Paint paintActiveBar;
    private int[] data = {60, 75, 68, 80, 72, 65, 78, 72, 70, 74};
    private int activeValue = 72;

    public HeartRateBarView(Context ctx) { super(ctx); init(); }
    public HeartRateBarView(Context ctx, AttributeSet a) { super(ctx, a); init(); }
    public HeartRateBarView(Context ctx, AttributeSet a, int d) { super(ctx, a, d); init(); }

    private void init() {
        paintBar = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBar.setColor(0x66E53935);   // translucent red

        paintActiveBar = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintActiveBar.setColor(0xFFE53935); // solid red
    }

    public void setData(int[] values, int currentValue) {
        data        = values;
        activeValue = currentValue;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.length == 0) return;

        float w = getWidth();
        float h = getHeight();

        int   n       = data.length;
        float barW    = (w / n) * 0.55f;
        float spacing = w / n;
        float cornerR = 4f;

        // Find max for scaling
        int max = 0;
        for (int v : data) if (v > max) max = v;
        if (max == 0) max = 1;

        for (int i = 0; i < n; i++) {
            float barH  = (data[i] / (float) max) * h;
            float left  = i * spacing + (spacing - barW) / 2f;
            float right = left + barW;
            float top   = h - barH;
            float bot   = h;

            RectF rect = new RectF(left, top, right, bot);
            boolean isActive = (data[i] == activeValue && i == n - 3); // highlight near-current
            canvas.drawRoundRect(rect, cornerR, cornerR, isActive ? paintActiveBar : paintBar);
        }
    }
}
