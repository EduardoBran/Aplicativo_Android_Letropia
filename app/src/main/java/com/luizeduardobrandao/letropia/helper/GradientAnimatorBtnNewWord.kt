package com.luizeduardobrandao.letropia.helper

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.luizeduardobrandao.letropia.R

object GradientAnimatorBtnNewWord {
    fun animate(
        button: MaterialButton,
        @ColorRes start: Int,
        @ColorRes end: Int,
        context: Context
    ) {
        val gradient = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                ContextCompat.getColor(context, start),
                ContextCompat.getColor(context, end)
            )
        ).apply {
            cornerRadius = context.resources.getDimension(R.dimen.corner_radius_btn_new_word)
        }

        button.backgroundTintList = null
        button.background = gradient

        ValueAnimator.ofFloat(0f, 1f, 0f).apply {
            duration = 5000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                val frac = anim.animatedValue as Float
                val c1 = ArgbEvaluator().evaluate(
                    frac,
                    ContextCompat.getColor(context, start),
                    ContextCompat.getColor(context, end)
                ) as Int
                val c2 = ArgbEvaluator().evaluate(
                    frac,
                    ContextCompat.getColor(context, end),
                    ContextCompat.getColor(context, start)
                ) as Int
                gradient.colors = intArrayOf(c1, c2)
            }
            start()
        }
    }
}