package com.example.thryve.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

/** Orange area / mountain chart for Strain Detail HR timeline. */
public class HrTimelineView extends View {

    private Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int[] data;

    public HrTimelineView(Context c) { super(c); init(); }
    public HrTimelineView(Context c, AttributeSet a) { super(c, a); init(); }
    public HrTimelineView(Context c, AttributeSet a, int d) { super(c, a, d); init(); }

    private void init() {
        line.setStyle(Paint.Style.STROKE); line.setStrokeWidth(3f); line.setColor(0xFFFF9800);
        fill.setStyle(Paint.Style.FILL);
    }

    public void setData(int[] d, int peak) { data = d; invalidate(); }

    @Override
    protected void onDraw(Canvas canvas) {
        if (data == null || data.length < 2) return;
        float w = getWidth(), h = getHeight(), n = data.length;
        fill.setShader(new LinearGradient(0,0,0,h,0xAAFF9800,0x00FF9800,Shader.TileMode.CLAMP));
        int max = 0, min = Integer.MAX_VALUE;
        for (int v : data) { if (v>max) max=v; if (v<min) min=v; }
        float rng = max-min==0?1:max-min, sx = w/(n-1);
        Path fp=new Path(), lp=new Path();
        for (int i=0; i<data.length; i++) {
            float x=i*sx, y=h-((data[i]-min)/rng)*h*0.85f-h*0.05f;
            if (i==0) { fp.moveTo(x,h); fp.lineTo(x,y); lp.moveTo(x,y); }
            else {
                float px=(i-1)*sx, py=h-((data[i-1]-min)/rng)*h*0.85f-h*0.05f, cpX=(px+x)/2f;
                fp.cubicTo(cpX,py,cpX,y,x,y); lp.cubicTo(cpX,py,cpX,y,x,y);
            }
        }
        fp.lineTo(w,h); fp.close();
        canvas.drawPath(fp,fill); canvas.drawPath(lp,line);
    }
}
