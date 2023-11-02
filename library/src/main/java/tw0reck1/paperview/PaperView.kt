/*
 * Copyright 2018 Adrian Tworkowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tw0reck1.paperview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import tw0reck1.paperview.PaperUtils.dpToPx

open class PaperView : View {

    object Type {
        const val NONE = 0
        const val HORIZONTAL = 1
        const val VERTICAL = 2
        const val GRID = HORIZONTAL or VERTICAL
    }

    private companion object {

        const val DEFAULT_PAPER_COLOR = Color.WHITE

        const val DEFAULT_BORDER_WIDTH = 2f
        const val DEFAULT_BORDER_COLOR = Color.BLACK

        const val DEFAULT_STROKE_WIDTH = 1f
        const val DEFAULT_STROKE_COLOR = Color.GRAY
        const val DEFAULT_STROKE_SPACING = 32f
        const val DEFAULT_STROKE_TYPE = Type.NONE

        const val UNSET_SIZE = -1f
    }

    private var paperColor = DEFAULT_PAPER_COLOR

    private var borderWidth: Float = UNSET_SIZE
    private var borderColor = DEFAULT_BORDER_COLOR

    private var strokeWidth: Float = UNSET_SIZE
    private var strokeColor = DEFAULT_STROKE_COLOR
    private var strokeSpacing: Float = UNSET_SIZE
    private var strokeType = DEFAULT_STROKE_TYPE

    private var paperBitmap: Bitmap? = null

    constructor(
        context: Context
    ) : super(context) {
        initDefaultAttributes()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initAttributes(context, attrs, 0)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initAttributes(context, attrs, defStyleAttr)
    }

    private fun initDefaultAttributes() {
        borderWidth = context.dpToPx(DEFAULT_BORDER_WIDTH)
        strokeWidth = context.dpToPx(DEFAULT_STROKE_WIDTH)
        strokeSpacing = context.dpToPx(DEFAULT_STROKE_SPACING)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val array = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PaperView,
            defStyleAttr,
            0
        )

        paperColor = array.getColor(R.styleable.PaperView_pv_paper_color, DEFAULT_PAPER_COLOR)

        borderWidth = array.getDimension(
            R.styleable.PaperView_pv_border_width,
            context.dpToPx(DEFAULT_BORDER_WIDTH)
        )
        borderColor = array.getColor(R.styleable.PaperView_pv_border_color, DEFAULT_BORDER_COLOR)

        strokeWidth = array.getDimension(
            R.styleable.PaperView_pv_stroke_width,
            context.dpToPx(DEFAULT_STROKE_WIDTH)
        )
        strokeColor = array.getColor(R.styleable.PaperView_pv_stroke_color, DEFAULT_STROKE_COLOR)
        strokeSpacing = array.getDimension(
            R.styleable.PaperView_pv_stroke_spacing,
            context.dpToPx(DEFAULT_STROKE_SPACING)
        )
        strokeType = array.getInteger(R.styleable.PaperView_pv_stroke_type, DEFAULT_STROKE_TYPE)

        array.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        paperBitmap = createPaperBitmap(w, h)
    }

    private fun createPaperBitmap(width: Int, height: Int): Bitmap {
        val bitmapWidth =  width - paddingRight - paddingLeft
        val bitmapHeight =  height - paddingBottom - paddingTop

        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(paperColor)

        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.strokeCap = Paint.Cap.SQUARE
        linePaint.style = Paint.Style.STROKE
        linePaint.color = strokeColor
        linePaint.strokeWidth = strokeWidth

        if (strokeType and Type.VERTICAL == Type.VERTICAL) {
            var lineX = 0f + strokeSpacing
            while (lineX <= bitmapWidth) {
                canvas.drawLine(lineX, 0f, lineX, bitmapHeight.toFloat(), linePaint)

                lineX += strokeSpacing
            }
        }

        if (strokeType and Type.HORIZONTAL == Type.HORIZONTAL) {
            var lineY = 0f + strokeSpacing
            while (lineY <= bitmapHeight) {
                canvas.drawLine(0f, lineY, bitmapWidth.toFloat(), lineY, linePaint)

                lineY += strokeSpacing
            }
        }

        if (borderWidth > 0f) {
            linePaint.color = borderColor
            linePaint.strokeWidth = borderWidth

            val offset = linePaint.strokeWidth / 2f

            canvas.drawRect(offset, offset, bitmapWidth - offset, bitmapHeight - offset, linePaint)
        }

        return bitmap
    }

    override fun onDraw(canvas: Canvas) {
        paperBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, paddingLeft.toFloat(), paddingTop.toFloat(), null)
        }
    }
}
