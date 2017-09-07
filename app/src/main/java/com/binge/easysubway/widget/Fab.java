package com.binge.easysubway.widget;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

import com.gordonwong.materialsheetfab.AnimatedFab;

/**
 * Created by Binge on 2017/9/7.
 */

public class Fab extends FloatingActionButton implements AnimatedFab {
    public Fab(Context context) {
        super(context);
    }

    public Fab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Fab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void show() {
        super.show();
        show(0,0);
    }

    @Override
    public void show(float translationX, float translationY) {
        setVisibility(VISIBLE);
    }
}
