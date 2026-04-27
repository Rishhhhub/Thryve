package com.example.thryve.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

/** Bar chart showing recent heart-rate readings. */
public class HeartRateBarView extends View {

    private Paint dim = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint bright = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int[] data = {60,75,68,80,72,65,78,72,70,74};
    private int active = 72;

    public HeartRateBarView(Context c) { super(c); init(); }
    public HeartRateBarView(Context c, AttributeSet a) { super(c, a); init(); }
    public HeartRateBarView(Context c, AttributeSet a, int d) { super(c, a, d); init(); }

    private void init() { dim.setColor(0x55E53935); bright.setColor(0xFFE53935); }

    public void setData(int[] vals, int cur) { data = vals; active = cur; invalidate(); }

    @Override
    protected void onDraw(Canvas canvas) {
        if (data == null || data.length == 0) return;
        float w = getWidth(), h = getHeight();
        float bw = (w / data.length) * 0.55f, sp = w / data.length;
        int max = 1; for (int v : data) if (v > max) max = v;
        for (int i = 0; i < data.length; i++) {
            float bh = (data[i]/(float)max)*h;
            float l = i*sp + (sp-bw)/2f;
            RectF r = new RectF(l, h-bh, l+bw, h);
            canvas.drawRoundRect(r, 4f, 4f,
                    (data[i]==active && i==data.length-3) ? bright : dim);
        }
    }
}
