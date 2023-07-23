package com.windowweather.android.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.windowweather.android.R;
import com.windowweather.android.util.DateUtils;

import java.util.Date;

public class WeatherSunView extends View {

    private static final float START_ANGLE = 180f;
    private static final float SWEEP_ANGLE = 180f;
    private static final float STROKE_WIDTH = 3f;
    private static final float SUN_SIZE = 50f;

    private String sunriseTime = "05:00";
    private String sunsetTime = "20:00";


    public WeatherSunView(Context context) {
        super(context);
    }

    public WeatherSunView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherSunView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSunriseTime(String sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public void setSunsetTime(String sunsetTime) {
        this.sunsetTime = sunsetTime;
        invalidate();
    }

    private float getSunPositionProgress() {
        Date sunriseDate = DateUtils.strToDate(sunriseTime);
        Date sunsetDate = DateUtils.strToDate(sunsetTime);
        Date now = new Date();

        if (now.before(sunriseDate) || now.after(sunsetDate)) {
            // 如果当前时间不在日出日落时间之间，则太阳位于左下角
            return 0f;
        } else {
            long totalDuration = sunsetDate.getTime() - sunriseDate.getTime();
            long elapsedTime = now.getTime() - sunriseDate.getTime();
            float progress = (float) elapsedTime / totalDuration;
            return progress;
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight();
        float arcRadius = Math.min(centerX, centerY) * 0.75f;
        float arcStartX = centerX - arcRadius;
        float arcStartY = centerY - arcRadius * 1.2f;
        float arcEndX = centerX + arcRadius;
        float arcEndY = centerY + arcRadius * 0.6f;

        RectF arcRect = new RectF();
        arcRect.set(arcStartX, arcStartY, arcEndX, arcEndY);

        Path arcPath = new Path();
        arcPath.addArc(arcRect, START_ANGLE, SWEEP_ANGLE);

        PathMeasure pathMeasure = new PathMeasure(arcPath, false);

        // 绘制圆弧
        Paint arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(10f);
        arcPaint.setDither(true);
        arcPaint.setAntiAlias(true);
        arcPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        // 绘制白色圆弧
        arcPaint.setColor(Color.parseColor("#A9A9A9"));
        canvas.drawPath(arcPath, arcPaint);
        // 绘制黄色圆弧
        arcPaint.setColor(Color.parseColor("#FFD700"));
        arcPath.reset();
        arcPath.addArc(arcRect, START_ANGLE, SWEEP_ANGLE * getSunPositionProgress());
        canvas.drawPath(arcPath, arcPaint);

        // 绘制直线
        Paint linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#A9A9A9"));
        linePaint.setStrokeWidth(3f);
        float lineStartX = centerX - arcRadius * 1.2f;
        float lineStartY = centerY - arcRadius * 0.3f;
        float lineEndX = centerX + arcRadius * 1.2f;
        float lineEndY = centerY - arcRadius * 0.3f;
        canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, linePaint);

        // 绘制太阳
        Paint sunPaint = new Paint();
        sunPaint.setAntiAlias(true);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable sunDrawable = getResources().getDrawable(R.drawable.w100);
        float[] pos = new float[2];
        pathMeasure.getPosTan(getSunPositionProgress() * pathMeasure.getLength(), pos, null);
        float sunLeft = pos[0] - (SUN_SIZE / 2f);
        float sunTop = pos[1] - (SUN_SIZE / 2f);
        float sunRight = pos[0] + (SUN_SIZE / 2f);
        float sunBottom = pos[1] + (SUN_SIZE / 2f);
        sunDrawable.setBounds((int) sunLeft, (int) sunTop, (int) sunRight, (int) sunBottom);
        sunDrawable.draw(canvas);

        // 绘制文本
        Paint textPaint = new Paint();
        textPaint.setStrokeWidth(7f);
        textPaint.setColor(Color.parseColor("#A9A9A9"));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(arcRadius * 0.10f);
        String sunriseStr = "日出  " + sunriseTime;
        String sunsetStr = "日落  " + sunsetTime;
        float textY = centerY - arcRadius * 0.3f;
        canvas.drawText(sunriseStr, centerX - arcRadius, textY * 1.125f, textPaint);
        canvas.drawText(sunsetStr, centerX + arcRadius, textY * 1.125f, textPaint);
    }
}