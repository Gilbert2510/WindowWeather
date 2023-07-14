package com.windowweather.android.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.windowweather.android.R;

public class ChangeColorUtils {
    private final int fadingHeight = 600; //当ScrollView滑动到什么位置时渐变消失（根据需要进行调整）
    private static final int START_ALPHA = 0;   //scrollview滑动开始位置
    private static final int END_ALPHA = 255;   //scrollview滑动结束位置

    public static void ChangeColorNight(ScrollView scrollView, LinearLayout linearLayout) {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();

                int maxScroll = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                float scrollRatio = (float) scrollY / maxScroll;
                int Red = (int) (scrollRatio * 5);
                int Green = (int) (scrollRatio * 24);
                int Blue = (int) (scrollRatio * 67);
                linearLayout.setBackgroundColor(Color.rgb(Red, Green, Blue));
            }
        });
    }

    public static void ChangeColor(ScrollView scrollView, LinearLayout linearLayout) {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();

                int maxScroll = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                float scrollRatio = (float) scrollY / maxScroll;
                linearLayout.setAlpha((1 - scrollRatio) * 1);
            }
        });
    }


//    public void ChangeBackgroundColor(ScrollView scrollView, LinearLayout linearLayout,boolean flag) {
//        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY > fadingHeight) {
//                    scrollY = fadingHeight;   //当滑动到指定位置之后设置颜色为纯色，之前的话要渐变---实现下面的公式即可
//                    if(flag) {
//                        //此时是白天
//                        linearLayout.setBackgroundResource(R.color.WhiteSmoke);
//                    } else {
//                        linearLayout.setBackgroundResource(R.color.JackieBlue);
//                    }
//                } else if (scrollY < 0) {
//                    scrollY = 0;
//                } else {
//                    if(flag) {
//                        //此时是白天
//                        linearLayout.setBackgroundResource(R.color.WhiteSmoke);
//                    } else {
//                        linearLayout.setBackgroundResource(R.color.JackieBlue);
//                    }
//                }
//                //drawable.setAlpha(scrollY * (END_ALPHA - START_ALPHA) / fadingHeight + START_ALPHA);
//            }
//        });
//    }


}
