package com.mindease.feature.analysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoodTrendChartView extends View {
    private static final int COLOR_POSITIVE = 0xFF74C7AF;
    private static final int COLOR_NEUTRAL = 0xFFF4C781;
    private static final int COLOR_NEGATIVE = 0xFFF08D9E;

    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dotStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint baselinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tooltipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    private List<Integer> points = new ArrayList<>(Arrays.asList(1, 0, -1, 0, 1, 0, 1));
    private List<String> labels = new ArrayList<>(Arrays.asList("Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon"));
    private List<String> details = new ArrayList<>(Arrays.asList("", "", "", "", "", "", ""));
    private int highlightedIndex = -1;

    public MoodTrendChartView(Context context) {
        super(context);
        init();
    }

    public MoodTrendChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoodTrendChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dp(4));
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(dp(9));
        glowPaint.setAlpha(60);
        glowPaint.setStrokeCap(Paint.Cap.ROUND);
        glowPaint.setStrokeJoin(Paint.Join.ROUND);

        dotFillPaint.setStyle(Paint.Style.FILL);
        dotStrokePaint.setStyle(Paint.Style.STROKE);
        dotStrokePaint.setStrokeWidth(dp(2));

        labelPaint.setColor(0xFF626AA8);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        baselinePaint.setColor(0x30A2ABDE);
        baselinePaint.setStrokeWidth(dp(1));

        tooltipPaint.setColor(0xEEF8FAFF);
        tooltipPaint.setStyle(Paint.Style.FILL);

        tooltipTextPaint.setColor(0xFF5160A1);
        tooltipTextPaint.setTextSize(sp(11));
    }

    public void setData(List<Integer> points, List<String> labels, List<String> details) {
        if (points != null && !points.isEmpty()) {
            this.points = new ArrayList<>(points);
        }
        this.labels = labels != null ? new ArrayList<>(labels) : new ArrayList<String>();
        this.details = details != null ? new ArrayList<>(details) : new ArrayList<String>();
        highlightedIndex = -1;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float left = dp(18);
        float right = width - dp(18);
        float top = dp(24);
        float bottom = height - dp(40);
        labelPaint.setTextSize(points.size() > 10 ? sp(9) : sp(12));

        canvas.drawLine(left, bottom, right, bottom, baselinePaint);
        if (points.isEmpty()) {
            return;
        }

        float step = points.size() == 1 ? 0f : (right - left) / (points.size() - 1);
        float centerY = (top + bottom) / 2f;
        float amplitude = (bottom - top) * 0.28f;

        path.reset();
        float previousX = left;
        float previousY = yForPoint(points.get(0), centerY, amplitude);
        path.moveTo(previousX, previousY);
        for (int i = 1; i < points.size(); i++) {
            float x = left + (step * i);
            float y = yForPoint(points.get(i), centerY, amplitude);
            float controlX = (previousX + x) / 2f;
            path.cubicTo(controlX, previousY, controlX, y, x, y);
            previousX = x;
            previousY = y;
        }

        glowPaint.setColor(0x239DB3FF);
        canvas.drawPath(path, glowPaint);

        previousX = left;
        previousY = yForPoint(points.get(0), centerY, amplitude);
        for (int i = 1; i < points.size(); i++) {
            float x = left + (step * i);
            float y = yForPoint(points.get(i), centerY, amplitude);
            linePaint.setColor(colorForValue(points.get(i)));
            Path segment = new Path();
            segment.moveTo(previousX, previousY);
            float controlX = (previousX + x) / 2f;
            segment.cubicTo(controlX, previousY, controlX, y, x, y);
            canvas.drawPath(segment, linePaint);
            previousX = x;
            previousY = y;
        }

        for (int i = 0; i < points.size(); i++) {
            float x = left + (step * i);
            float y = yForPoint(points.get(i), centerY, amplitude);
            int color = colorForValue(points.get(i));
            dotFillPaint.setColor(i == highlightedIndex ? 0xFFFFFFFF : 0xFFF8F8FF);
            dotStrokePaint.setColor(color);
            canvas.drawCircle(x, y, i == highlightedIndex ? dp(7) : dp(6), dotFillPaint);
            canvas.drawCircle(x, y, i == highlightedIndex ? dp(7) : dp(6), dotStrokePaint);

            if (i < labels.size() && labels.get(i) != null && !labels.get(i).isEmpty()) {
                canvas.drawText(labels.get(i), x, height - dp(12), labelPaint);
            }
        }

        if (highlightedIndex >= 0 && highlightedIndex < points.size()) {
            drawTooltip(canvas, left + (step * highlightedIndex), yForPoint(points.get(highlightedIndex), centerY, amplitude));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (points.isEmpty()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                highlightedIndex = nearestIndex(event.getX());
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                highlightedIndex = -1;
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private int nearestIndex(float touchX) {
        float left = dp(18);
        float right = getWidth() - dp(18);
        float step = points.size() == 1 ? 0f : (right - left) / (points.size() - 1);
        int closest = 0;
        float bestDistance = Float.MAX_VALUE;
        for (int i = 0; i < points.size(); i++) {
            float x = left + (step * i);
            float distance = Math.abs(x - touchX);
            if (distance < bestDistance) {
                bestDistance = distance;
                closest = i;
            }
        }
        return closest;
    }

    private void drawTooltip(Canvas canvas, float pointX, float pointY) {
        String title = highlightedIndex < labels.size() && !labels.get(highlightedIndex).isEmpty()
                ? labels.get(highlightedIndex)
                : "Day " + (highlightedIndex + 1);
        String body = highlightedIndex < details.size() ? details.get(highlightedIndex) : "";
        String[] lines = body == null || body.isEmpty() ? new String[]{title} : new String[]{title, body};

        float boxWidth = dp(124);
        float lineHeight = sp(12);
        float boxHeight = dp(16) + (lineHeight * lines.length * 1.2f);
        float left = Math.max(dp(8), Math.min(pointX - (boxWidth / 2f), getWidth() - boxWidth - dp(8)));
        float top = Math.max(dp(8), pointY - boxHeight - dp(14));
        RectF rect = new RectF(left, top, left + boxWidth, top + boxHeight);
        canvas.drawRoundRect(rect, dp(16), dp(16), tooltipPaint);

        float textY = top + dp(18);
        canvas.drawText(title, left + dp(12), textY, tooltipTextPaint);
        if (lines.length > 1) {
            for (String detailLine : body.split("\n")) {
                textY += lineHeight * 1.2f;
                canvas.drawText(detailLine, left + dp(12), textY, tooltipTextPaint);
            }
        }
    }

    private float yForPoint(int value, float centerY, float amplitude) {
        if (value > 0) {
            return centerY - amplitude;
        }
        if (value < 0) {
            return centerY + amplitude;
        }
        return centerY;
    }

    private int colorForValue(int value) {
        if (value > 0) {
            return COLOR_POSITIVE;
        }
        if (value < 0) {
            return COLOR_NEGATIVE;
        }
        return COLOR_NEUTRAL;
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
