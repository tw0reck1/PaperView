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
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

open class SketchyPaperView : PaperView, View.OnTouchListener {

    private companion object {

        const val DEFAULT_SKETCH_STROKE_WIDTH = 1.5f
        const val DEFAULT_SKETCH_STROKE_COLOR = Color.BLACK

        const val UNSET_SIZE = -1f

        private fun dpToPx(context: Context, dp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                    context.resources.getDisplayMetrics())
        }

    }

    private var sketchStrokeWidth: Float = UNSET_SIZE
    private var sketchStrokeColor = DEFAULT_SKETCH_STROKE_COLOR

    private val sketchPath = Path()
    private val sketchPathPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttributes(context, attrs, 0)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initAttributes(context, attrs, defStyleAttr)
        init()
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val array = context.theme.obtainStyledAttributes(attrs,
                R.styleable.SketchyPaperView, defStyleAttr, 0)

        sketchStrokeWidth = array.getDimension(R.styleable.SketchyPaperView_spv_sketch_stroke_width,
                dpToPx(context, DEFAULT_SKETCH_STROKE_WIDTH))
        sketchStrokeColor = array.getColor(R.styleable.SketchyPaperView_spv_sketch_stroke_color,
                DEFAULT_SKETCH_STROKE_COLOR)

        array.recycle()
    }

    private fun init() {
        sketchPath.setFillType(Path.FillType.EVEN_ODD);

        sketchPathPaint.style = Paint.Style.STROKE
        sketchPathPaint.color = sketchStrokeColor
        sketchPathPaint.strokeWidth = sketchStrokeWidth
        sketchPathPaint.strokeCap = Paint.Cap.ROUND
        sketchPathPaint.strokeJoin = Paint.Join.ROUND

        setOnTouchListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawPath(sketchPath, sketchPathPaint)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event == null) return false

        if (event.action == MotionEvent.ACTION_DOWN) {
            sketchPath.moveTo(event.x, event.y)

            return true
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            sketchPath.lineTo(event.x, event.y)
            invalidate()

            return true
        }

        return false
    }

}