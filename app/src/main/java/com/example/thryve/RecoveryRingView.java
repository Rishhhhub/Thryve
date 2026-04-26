package com.example.thryve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Single thick arc ring for the Recovery Detail screen.
 */
public class RecoveryRingView extends View {

    private Paint paintTrack;
    private Paint paintArc;
    private float progress = 0.85f;

    private static final float STROKE_WIDTH = 18f;

    public RecoveryRingView(Context ctx) { super(ctx); init(); }
    public RecoveryRingView(Context ctx, AttributeSet a) { super(ctx, a); init(); }
    public RecoveryRingView(Context ctx, AttributeSet a, int d) { super(ctx, a, d); init(); }

    private void init() {
        paintTrack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTrack.setStyle(Paint.Style.STROKE);
        paintTrack.setStrokeWidth(STROKE_WIDTH);
        paintTrack.setColor(0x22FFFFFF);

        paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setStrokeWidth(STROKE_WIDTH);
        paintArc.setStrokeCap(Paint.Cap.ROUND);
        paintArc.setColor(0xFF4CAF50);
    }

    public void setProgress(float progress, int color) {
        this.progress = Math.max(0f, Math.min(1f, progress));
        paintArc.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float r  = Math.min(cx, cy) - STROKE_WIDTH;

        RectF oval = new RectF(cx - r, cy - r, cx + r, cy + r);
        canvas.drawArc(oval, -90, 360, false, paintTrack);
        canvas.drawArc(oval, -90, 360 * progress, false, paintArc);
    }
}
