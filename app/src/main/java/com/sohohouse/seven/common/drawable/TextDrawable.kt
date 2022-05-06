package com.sohohouse.seven.common.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.sohohouse.seven.R

class TextDrawable(
    context: Context,
    private var text: String,
    color: Int,
    @FontRes typeFace: Int = R.font.faro_lucky_regular,
    @DimenRes textSize: Int = R.dimen.faro_reg_lucky_12sp
) : Drawable() {
    private val paint: Paint = Paint()

    init {
        val typeface = ResourcesCompat.getFont(context, typeFace)
        paint.typeface = typeface
        paint.color = color
        paint.textSize = context.resources.getDimensionPixelSize(textSize).toFloat()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        canvas.drawText(
            text,
            (bounds.width() / 2).toFloat(),
            (bounds.height() / 2) + VERTICAL_OFFSET,
            paint
        )
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    companion object {
        private const val VERTICAL_OFFSET = 17.8f
    }
}