package com.nara.bacayuk.writing.quiz.tracing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.createBitmap
import android.graphics.PointF

class DrawingQuizView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isDrawing = false
    private val path = Path()

    private var paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 30f
    }

    private var pencil = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 30f
    }

    private val strokes = mutableListOf<MutableList<PointF>>()
    private var currentStroke: MutableList<PointF>? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, pencil)
    }

    fun setDrawingMode(enabled: Boolean) {
        isDrawing = enabled
//        paint = pencil
    }

    fun clearCanvas() {
        path.reset()
        strokes.clear()
        invalidate()
    }

    fun getBitmap(): Bitmap {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                currentStroke = mutableListOf(PointF(x, y))
                strokes.add(currentStroke!!)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                currentStroke?.add(PointF(x, y))
            }
            MotionEvent.ACTION_UP -> {
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun getStrokes(): List<List<PointF>> = strokes
}
