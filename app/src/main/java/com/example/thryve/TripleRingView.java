package com.example.thryve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Draws three concentric arc rings: outer (green/recovery),
 * middle (orange/strain), inner (blue/sleep).
 */
public class TripleRingView extends View {

    private Paint paintTrack;
    private Paint paintOuter;
    private Paint paintMiddle;
    private Paint paintInner;

    private float outerProgress  = 0.85f;
    private float middleProgress = 0.72f;
    private float innerProgress  = 0.78f;

    private int outerColor  = 0xFF4CAF50;
    private int middleColor = 0xFFFF9800;
    private int innerColor  = 0xFF2196F3;

    private static final float STROKE_WIDTH = 14f;
    private static final float GAP          = 20f;

    public TripleRingView(Context ctx) { super(ctx); init(); }
    public TripleRingView(Context ctx, AttributeSet a) { super(ctx, a); init(); }
    public TripleRingView(Context ctx, AttributeSet a, int d) { super(ctx, a, d); init(); }

    private void init() {
        paintTrack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTrack.setStyle(Paint.Style.STROKE);
        paintTrack.setStrokeWidth(STROKE_WIDTH);
        paintTrack.setStrokeCap(Paint.Cap.ROUND);
        paintTrack.setColor(0x22FFFFFF);

        paintOuter = makePaint(outerColor);
        paintMiddle = makePaint(middleColor);
        paintInner = makePaint(innerColor);
    }

    private Paint makePaint(int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(STROKE_WIDTH);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setColor(color);
        return p;
    }

    public void setRingValues(float outer, float middle, float inner) {
        outerProgress  = clamp(outer);
        middleProgress = clamp(middle);
        innerProgress  = clamp(inner);
        invalidate();
    }

    public void setRingColors(int outer, int middle, int inner) {
        paintOuter.setColor(outer);
        paintMiddle.setColor(middle);
        paintInner.setColor(inner);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth()  / 2f;
        float cy = getHeight() / 2f;
        float maxRadius = Math.min(cx, cy) - STROKE_WIDTH;

        // Outer ring
        drawRing(canvas, cx, cy, maxRadius, paintTrack, outerProgress, paintOuter);
        // Middle ring
        drawRing(canvas, cx, cy, maxRadius - STROKE_WIDTH - GAP, paintTrack, middleProgress, paintMiddle);
        // Inner ring
        drawRing(canvas, cx, cy, maxRadius - (STROKE_WIDTH + GAP) * 2, paintTrack, innerProgress, paintInner);
    }

    private void drawRing(Canvas c, float cx, float cy, float r,
                          Paint track, float progress, Paint arc) {
        RectF oval = new RectF(cx - r, cy - r, cx + r, cy + r);
        c.drawArc(oval, -90, 360, false, track);
        c.drawArc(oval, -90, 360 * progress, false, arc);
    }

    private float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }
}
