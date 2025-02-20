package com.example.basic;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {
    private Paint paint;
    private Paint textPaint;
    private RectF rectF;
    private String[] labels = {"Vitamin A", "Vitamin B", "Vitamin D", "Vitamin E", "Iron", "Calcium"};
    private int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.parseColor("#9C27B0"), Color.CYAN, Color.MAGENTA};
    private float[] values = {30, 40, 20, 50, 20, 40};
    private float totalValue;

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        totalValue = 0;
        for (float value : values) {
            totalValue += value;
        }
        paint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;
        float centerX = width / 2f;
        float centerY = height / 2f;
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        float startAngle = 0;

        for (int i = 0; i < labels.length; i++) {
            float percentage = (values[i] / totalValue) * 100;
            float sweepAngle = (values[i] / totalValue) * 360;
            paint.setColor(colors[i]);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
            float angle = (float) Math.toRadians(startAngle + sweepAngle / 2);
            float textX = centerX + (float) ((radius / 2) * Math.cos(angle));
            float textY = centerY + (float) ((radius / 2) * Math.sin(angle));
            float percentageY = textY + 60;
            canvas.drawText(labels[i], textX, textY, textPaint);
            canvas.drawText(String.format("%.1f%%", percentage), textX, percentageY, textPaint);
            startAngle += sweepAngle;
        }
    }
}