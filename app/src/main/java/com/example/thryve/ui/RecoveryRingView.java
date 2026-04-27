package com.example.thryve.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

/** Single thick arc ring for the Recovery Detail screen. */
public class RecoveryRingView extends View {

    private Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint arc   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress = 0.85f;
    private static final float SW = 18f;

    public RecoveryRingView(Context c) { super(c); init(); }
    public RecoveryRingView(Context c, AttributeSet a) { super(c, a); init(); }
    public RecoveryRingView(Context c, AttributeSet a, int d) { super(c, a, d); init(); }

    private void init() {
        track.setStyle(Paint.Style.STROKE); track.setStrokeWidth(SW); track.setColor(0x22FFFFFF);
        arc.setStyle(Paint.Style.STROKE); arc.setStrokeWidth(SW);
        arc.setStrokeCap(Paint.Cap.ROUND); arc.setColor(0xFF4CAF50);
    }

    public void setProgress(float p, int color) {
        progress = Math.max(0f, Math.min(1f, p)); arc.setColor(color); invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cx=getWidth()/2f, cy=getHeight()/2f, r=Math.min(cx,cy)-SW;
        RectF rf = new RectF(cx-r, cy-r, cx+r, cy+r);
        canvas.drawArc(rf, -90, 360, false, track);
        canvas.drawArc(rf, -90, 360*progress, false, arc);
    }
}
