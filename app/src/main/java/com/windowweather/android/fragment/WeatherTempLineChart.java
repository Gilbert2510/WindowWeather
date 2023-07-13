package com.windowweather.android.fragment;


import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherTempLineChart {
    public static void initHourlyLineChart(Context context, LineChart lineChart, int[] data) {
        // 禁用描述文本
        lineChart.getDescription().setEnabled(false);
        // 启用触摸手势
        lineChart.setTouchEnabled(true);
        //隐藏图例
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        //隐藏边线
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.getLegend().setEnabled(false);
        //隐藏高亮十字
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        int count = (int) Arrays.stream(data).distinct().count();
        setYAxis(lineChart, count);
        setXAxis(lineChart);

        // 启用拖动
        lineChart.setDragEnabled(true);
        // 启用缩放
        lineChart.setScaleEnabled(false);
        // lineChart.setScaleXEnabled(true);
        // lineChart.setScaleYEnabled(true);

        // 沿两个轴缩放
        lineChart.setPinchZoom(false);
        // 动画
        lineChart.animateXY(2000, 2000, Easing.EaseInOutCirc);


        setData(lineChart, data);
    }

    //加载数据
    public static void setData(LineChart lineChart, int[] data) {
        //在MPAndroidChart一般都是通过List<Entry>对象来装数据的
        List<Entry> entries = new ArrayList<Entry>();
        //循环取出数据
        for (int i = 0; i < data.length; i++) {
            entries.add(new Entry(i, data[i]));
        }
        //一个LineDataSet对象就是一条曲线
        LineDataSet lineDataSet = new LineDataSet(entries, "曲线");

        lineDataSet.setCircleRadius(4);//设置圆点半径大小
        lineDataSet.setLineWidth(4);//设置折线的宽度
        lineDataSet.setCircleColor(Color.parseColor("#FFFFFF"));//一次性设置所有圆点的颜色
        lineDataSet.setDrawCircleHole(true);//设置是否空心
        lineDataSet.setCircleHoleColor(Color.parseColor("#FF6347"));

        //lineDataSet.setCircleColors(Color.RED,Color.BLACK,Color.GREEN);//依次设置每个圆点的颜色

        //lineDataSet.setFillColor(Color.GRAY);//设置线条下方的填充

        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // 设置折线类型，这里设置为贝塞尔曲线
        lineDataSet.setCubicIntensity(0.2f);//设置曲线的Mode强度，0-1
        lineDataSet.setDrawFilled(false);//是否绘制折线下方的填充
        lineDataSet.setColor(Color.parseColor("#FF6347"));//设置折线的颜色,有三个构造方法
        lineDataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        lineDataSet.setValueTextSize(12f);
        //LineData才是正真给LineChart的数据
        LineData lineData = new LineData(lineDataSet);
        lineData.setValueFormatter(new IntegerValueFormatter());
        lineChart.setData(lineData);
    }

    public static void setYAxis(LineChart lineChart, int count) {
        //不显示右侧的Y轴
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisRight().setEnabled(false);
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);//横轴线
        yAxis.setLabelCount(count, true);//显示纵轴个数
        yAxis.setXOffset(10);//设置距离
    }

    public static void setXAxis(LineChart lineChart) {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);//纵向网格线
        xAxis.setDrawAxisLine(false);//顶部边线
    }

}
