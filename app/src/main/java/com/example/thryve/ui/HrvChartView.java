package com.example.thryve.ui;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

/** Blue spline/line chart for Recovery Detail HRV. */
public class HrvChartView extends View {

    private Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float[] data;

    public HrvChartView(Context c) { super(c); init(); }
    public HrvChartView(Context c, AttributeSet a) { super(c, a); init(); }
    public HrvChartView(Context c, AttributeSet a, int d) { super(c, a, d); init(); }

    private void init() {
        line.setStyle(Paint.Style.STROKE); line.setStrokeWidth(4f); line.setColor(0xFF2196F3);
        fill.setStyle(Paint.Style.FILL);
    }

    public void setData(float[] d) { data = d; invalidate(); }

    @Override
    protected void onDraw(Canvas canvas) {
        if (data == null || data.length < 2) return;
        float w = getWidth(), h = getHeight(), n = data.length;
        fill.setShader(new LinearGradient(0,0,0,h,0x882196F3,0x002196F3,Shader.TileMode.CLAMP));
        float max=0, min=Float.MAX_VALUE;
        for (float v : data) { if (v>max) max=v; if (v<min) min=v; }
        float rng = max-min==0?1:max-min, sx = w/(n-1);
        Path fp=new Path(), lp=new Path();
        for (int i=0; i<data.length; i++) {
            float x=i*sx, y=h-((data[i]-min)/rng)*h*0.7f-h*0.15f;
            if (i==0) { fp.moveTo(x,h); fp.lineTo(x,y); lp.moveTo(x,y); }
            else {
                float px=(i-1)*sx, py=h-((data[i-1]-min)/rng)*h*0.7f-h*0.15f, cpX=(px+x)/2f;
                fp.cubicTo(cpX,py,cpX,y,x,y); lp.cubicTo(cpX,py,cpX,y,x,y);
            }
        }
        fp.lineTo(w,h); fp.close();
        canvas.drawPath(fp,fill); canvas.drawPath(lp,line);
    }
}
