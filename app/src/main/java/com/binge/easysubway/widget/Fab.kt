package com.binge.easysubway.widget

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import com.gordonwong.materialsheetfab.AnimatedFab

/**
 * Created by Administrator on 2017/9/4.
 */
class Fab : FloatingActionButton, AnimatedFab {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun show() {
        show(0f, 0f)
    }

    override fun show(translationX: Float, translationY: Float) {
        visibility = View.VISIBLE
    }


}