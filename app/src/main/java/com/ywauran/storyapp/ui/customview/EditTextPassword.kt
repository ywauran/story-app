package com.ywauran.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class EditTextPassword: AppCompatEditText, View.OnTouchListener {

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

    private fun init() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


            }
            override fun afterTextChanged(s: Editable) {
                isError(s)
            }
        })
    }

    private fun isError(s: Editable) {
        error = if (s.toString().isNotEmpty()) {
            if (s.length < 8) {
                "Password must be at least 8 characters long!"
            } else {
                null
            }
        } else {
            null
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textSize = 12f
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

}