package com.ywauran.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.ywauran.storyapp.R

class ButtonSecondaryStoryApp: AppCompatButton {

    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable
    private var txtColorPrimary: Int = 0
    private var txtColorSecondary: Int = 0
    private var txtColor:Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttrs: Int) : super(
        context,
        attrs,
        defStyleAttrs
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if(isEnabled) enabledBackground else disabledBackground
        txtColor = if (isEnabled) txtColorSecondary else txtColorPrimary
        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
    }

    private fun init() {
        txtColorPrimary = ContextCompat.getColor(context, android.R.color.white)
        txtColorSecondary = ContextCompat.getColor(context, R.color.color_black)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_secondary) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }
}