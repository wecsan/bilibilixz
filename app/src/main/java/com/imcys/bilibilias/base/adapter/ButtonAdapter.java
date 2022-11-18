package com.imcys.bilibilias.base.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.databinding.BindingConversion;

/**
 * @author:imcys
 * @create: 2022-11-17 15:45
 * @Description: Button视图绑定器
 */
public class ButtonAdapter {


    @BindingConversion
    public static ColorDrawable setBackground(int color) {
        return new ColorDrawable(color);
    }

    @BindingConversion
    public static ColorDrawable setBackground(String colorStr) {
        int color = Color.parseColor(colorStr);
        return new ColorDrawable(color);

    }

}