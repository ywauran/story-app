package com.ywauran.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.ywauran.storyapp.R

class EditTextEmail: AppCompatEditText, View.OnTouchListener {
    private lateinit var clearButtonImage: Drawable
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
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_close) as Drawable
        setOnTouchListener(this)

        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
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
        val string = s.toString().trim()
        error = if (string.isNotEmpty()) {
            if(isValid(string)) {
                null
            } else {
                "Invalid email address."
            }
        } else {
            null
        }
    }

    private fun isValid(email: String): Boolean{
        return if (TextUtils.isEmpty(email)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = "Enter your email address"
        textSize = 12f
    }
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }
}