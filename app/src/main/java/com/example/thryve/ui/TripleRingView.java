package com.example.thryve.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

/** Three concentric arc rings for the dashboard readiness indicator. */
public class TripleRingView extends View {

    private Paint track, outer, middle, inner;
    private float pOuter = 0.85f, pMiddle = 0.72f, pInner = 0.78f;
    private static final float SW = 14f, GAP = 20f;

    public TripleRingView(Context c) { super(c); init(); }
    public TripleRingView(Context c, AttributeSet a) { super(c, a); init(); }
    public TripleRingView(Context c, AttributeSet a, int d) { super(c, a, d); init(); }

    private void init() {
        track = makePaint(0x22FFFFFF, false);
        outer  = makePaint(0xFF4CAF50, true);
        middle = makePaint(0xFFFF9800, true);
        inner  = makePaint(0xFF2196F3, true);
    }

    private Paint makePaint(int color, boolean cap) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(SW);
        if (cap) p.setStrokeCap(Paint.Cap.ROUND);
        p.setColor(color);
        return p;
    }

    public void setRingValues(float o, float m, float i) {
        pOuter = clamp(o); pMiddle = clamp(m); pInner = clamp(i); invalidate();
    }

    public void setRingColors(int oc, int mc, int ic) {
        outer.setColor(oc); middle.setColor(mc); inner.setColor(ic); invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cx = getWidth()/2f, cy = getHeight()/2f;
        float maxR = Math.min(cx, cy) - SW;
        arc(canvas, cx, cy, maxR,               pOuter,  outer);
        arc(canvas, cx, cy, maxR - SW - GAP,    pMiddle, middle);
        arc(canvas, cx, cy, maxR - (SW+GAP)*2,  pInner,  inner);
    }

    private void arc(Canvas c, float cx, float cy, float r, float prog, Paint paint) {
        RectF rf = new RectF(cx-r, cy-r, cx+r, cy+r);
        c.drawArc(rf, -90, 360, false, track);
        c.drawArc(rf, -90, 360*prog, false, paint);
    }

    private float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }
}
