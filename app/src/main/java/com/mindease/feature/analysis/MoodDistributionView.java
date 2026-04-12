package com.mindease.feature.analysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class MoodDistributionView extends View {
    private static final int COLOR_POSITIVE = 0xFF74C7AF;
    private static final int COLOR_NEUTRAL = 0xFFF4C781;
    private static final int COLOR_NEGATIVE = 0xFFF08D9E;

    private final Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hazePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcBounds = new RectF();

    private int positive;
    private int neutral;
    private int negative;

    public MoodDistributionView(Context context) {
        super(context);
        init();
    }

    public MoodDistributionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoodDistributionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeCap(Paint.Cap.BUTT);
        ringPaint.setStrokeWidth(dp(30));

        innerPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(0xFF5C63A3);
        textPaint.setTextAlign(Paint.Align.CENTER);
        centerStrokePaint.setStyle(Paint.Style.STROKE);
        centerStrokePaint.setStrokeWidth(dp(2));
        centerStrokePaint.setColor(0x66FFFFFF);
        hazePaint.setStyle(Paint.Style.FILL);
    }

    public void setData(int positive, int neutral, int negative) {
        this.positive = positive;
        this.neutral = neutral;
        this.negative = negative;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float cx = width / 2f;
        float cy = height / 2f;
        float radius = Math.min(width, height) * 0.33f;

        hazePaint.setColor(0x20A5D9FF);
        canvas.drawCircle(cx - dp(26), cy + dp(12), radius * 1.15f, hazePaint);
        hazePaint.setColor(0x20F3C4D9);
        canvas.drawCircle(cx + dp(34), cy - dp(20), radius * 1.05f, hazePaint);

        int total = Math.max(1, positive + neutral + negative);
        float startAngle = -90f;
        arcBounds.set(cx - radius, cy - radius, cx + radius, cy + radius);

        float positiveSweep = positive * 360f / total;
        float neutralSweep = neutral * 360f / total;
        float negativeSweep = negative * 360f / total;

        drawArc(canvas, startAngle, positiveSweep, COLOR_POSITIVE);
        drawArc(canvas, startAngle + positiveSweep, neutralSweep, COLOR_NEUTRAL);
        drawArc(canvas, startAngle + positiveSweep + neutralSweep, negativeSweep, COLOR_NEGATIVE);

        innerPaint.setColor(0xEEF7F8FF);
        canvas.drawCircle(cx, cy, radius - dp(34), innerPaint);
        innerPaint.setColor(0x40C8DFFF);
        canvas.drawCircle(cx - dp(10), cy + dp(8), radius - dp(62), innerPaint);
        canvas.drawCircle(cx, cy, radius - dp(34), centerStrokePaint);

        textPaint.setTextSize(sp(18));
        drawPercent(canvas, cx, cy, radius, startAngle, positiveSweep, positive, total);
        drawPercent(canvas, cx, cy, radius, startAngle + positiveSweep, neutralSweep, neutral, total);
        drawPercent(canvas, cx, cy, radius, startAngle + positiveSweep + neutralSweep, negativeSweep, negative, total);
    }

    private void drawArc(Canvas canvas, float startAngle, float sweepAngle, int color) {
        ringPaint.setColor(color);
        canvas.drawArc(arcBounds, startAngle, sweepAngle, false, ringPaint);
    }

    private void drawPercent(Canvas canvas, float cx, float cy, float radius, float startAngle, float sweepAngle, int value, int total) {
        if (value <= 0) {
            return;
        }
        double radians = Math.toRadians(startAngle + (sweepAngle / 2f));
        float textRadius = radius + dp(6);
        float x = cx + (float) (Math.cos(radians) * textRadius);
        float y = cy + (float) (Math.sin(radians) * textRadius) + dp(6);
        int percent = Math.round(value * 100f / total);
        canvas.drawText(percent + "%", x, y, textPaint);
    }

    private float dp(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }

    private float sp(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                value,
                getResources().getDisplayMetrics()
        );
    }
}
