package com.nara.bacayuk.writing.letter.tracing.lowercase

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.pow
import kotlin.math.sqrt

class DrawingLetterLowercaseView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var currentPath = Path()
    private var templatePath = Path()
    private var userPath = mutableListOf<Path>()
    private val allUserPoints = mutableListOf<PointF>()
    private val templatePoints = mutableListOf<PointF>()
    private var pathMeasure = PathMeasure()

    private var isDrawing = false
    private var onCorrectTracing: (() -> Unit)? = null
    private var currentLetter = "a"

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f

    private var outlineBitmap: Bitmap? = null
    private var filledBitmap: Bitmap? = null

    private var paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 30f
    }

    private val eraser = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 30f
    }

    private val templatePaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        loadLetterDrawables(currentLetter)
    }

    fun setDrawingMode(enabled: Boolean) {
        isDrawing = enabled
    }

    fun setDrawingEraser(enabled: Boolean) {
        isDrawing = enabled
        paint = eraser
    }

    fun clearCanvas() {
        userPath.clear()
        allUserPoints.clear()
        invalidate()
    }

    fun setOnCorrectTracingListener(listener: () -> Unit) {
        onCorrectTracing = listener
    }

    fun setLetter(letter: String) {
        currentLetter = letter
        createTemplatePath(currentLetter, viewWidth, viewHeight)
        loadLetterDrawables(currentLetter)
        invalidate()
    }

    private fun loadLetterDrawables(letter: String) {
        if (width <= 0 || height <= 0) return

        val outlineResourceName = "huruf_${letter.lowercase()}_lowercase_outline"
        val filledResourceName = "huruf_${letter.lowercase()}_lowercase_fill"

        val outlineResourceId = context.resources.getIdentifier(
            outlineResourceName, "drawable", context.packageName
        )
        val filledResourceId = context.resources.getIdentifier(
            filledResourceName, "drawable", context.packageName
        )

        outlineBitmap = if (outlineResourceId != 0) {
            val drawable = ContextCompat.getDrawable(context, outlineResourceId)
            drawable?.toBitmap(width, height, Bitmap.Config.ARGB_8888)
        } else {
            null
        }

        filledBitmap = if (filledResourceId != 0) {
            val drawable = ContextCompat.getDrawable(context, filledResourceId)
            drawable?.toBitmap(width, height, Bitmap.Config.ARGB_8888)
        } else {
            null
        }
    }

    private fun createTemplatePath(letter: String, canvasWidth: Float, canvasHeight: Float) {
        templatePath.reset()

        val w = canvasWidth
        val h = canvasHeight

        when (letter) {
            "a" -> {
                templatePath.moveTo(w * 0.2f, h * 0.5f)
                templatePath.quadTo(w * 0.8f, h * 0.2f, w * 0.8f, h * 0.8f)
                templatePath.moveTo(w * 0.78f, h * 0.6f)
                templatePath.quadTo(
                    w * 0.5f, h * 0.56f,
                    w * 0.2f, h * 0.68f
                )
                templatePath.quadTo(
                    w * 0.2f, h * 0.9f,
                    w * 0.78f, h * 0.75f
                )
            }
            "b" -> {
                templatePath.moveTo(w * 0.2f, h * 0.22f)
                templatePath.lineTo(w * 0.2f, h * 0.8f)
                templatePath.moveTo(w * 0.2f, h * 0.5f)
                templatePath.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.85f, h * 0.8f), 180f, 180f, false)
                templatePath.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.85f, h * 0.8f), 180f, -180f, false)
            }
            "c" -> {
                templatePath.moveTo(w * 0.8f, h * 0.5f)
                templatePath.quadTo(w * 0.2f, h * 0.35f,
                    w * 0.18f, h * 0.55f)
                templatePath.quadTo(w * 0.18f, h * 0.9f,
                    w * 0.8f, h * 0.75f)
            }
            "d" -> {
                templatePath.moveTo(w * 0.8f, h * 0.2f)
                templatePath.lineTo(w * 0.8f, h * 0.8f)
                templatePath.moveTo(w * 0.8f, h * 0.5f)
                templatePath.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.8f, h * 0.8f), 180f, -180f, false)
                templatePath.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.8f, h * 0.8f), 180f, 180f, false)
            }
            "e" -> {
                templatePath.moveTo(w * 0.2f, h * 0.6f)
                templatePath.lineTo(w * 0.8f, h * 0.6f)
                templatePath.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.8f, h * 0.8f), 180f, 180f, false)
                templatePath.moveTo(w * 0.2f, h * 0.6f)
                templatePath.quadTo(w * 0.2f, h * 0.9f, w * 0.75f, h * 0.75f)
            }
            "f" -> {
                templatePath.moveTo(w * 0.85f, h * 0.25f)
                templatePath.quadTo(w * 0.6f, h * 0.2f, w * 0.45f, h * 0.4f)
                templatePath.lineTo(w * 0.45f, h * 0.8f)
                templatePath.moveTo(w * 0.15f, h * 0.42f)
                templatePath.lineTo(w * 0.85f, h * 0.42f)

            }
            "g" -> {
                templatePath.moveTo(w * 0.8f, h * 0.4f)
                templatePath.lineTo(w * 0.8f, h * 0.9f)

                templatePath.addOval(w * 0.16f, h * 0.4f, w * 0.8f, h * 0.75f, Path.Direction.CW)

                templatePath.moveTo(w * 0.8f, h * 0.9f)
                templatePath.quadTo(w * 0.5f, h * 1f, w * 0.2f, h * 0.88f)

            }
            "h" -> {
                templatePath.moveTo(w * 0.2f, h * 0.2f)
                templatePath.lineTo(w * 0.2f, h * 0.8f)

                templatePath.moveTo(w * 0.2f, h * 0.5f)
                templatePath.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.8f, h * 0.8f), 180f, 180f, false)

                templatePath.lineTo(w * 0.8f, h * 0.8f)
            }
            "i" -> {
                templatePath.moveTo(w * 0.5f, h * 0.4f)
                templatePath.lineTo(w * 0.5f, h * 0.8f)
                templatePath.addOval(w * 0.48f, h * 0.25f, w * 0.53f, h * 0.3f, Path.Direction.CW)
            }
            "j" -> {
                templatePath.moveTo(w * 0.62f, h * 0.4f)
                templatePath.lineTo(w * 0.62f, h * 0.83f)
                templatePath.quadTo(w * 0.62f, h * 0.98f, w * 0.15f, h * 0.95f)
                templatePath.addOval(w * 0.58f, h * 0.25f, w * 0.64f, h * 0.3f, Path.Direction.CW)
            }
            "k" -> {
                templatePath.moveTo(w * 0.2f, h * 0.26f)
                templatePath.lineTo(w * 0.2f, h * 0.8f)

                templatePath.moveTo(w * 0.2f, h * 0.7f)
                templatePath.lineTo(w * 0.83f, h * 0.38f)

                templatePath.moveTo(w * 0.46f, h * 0.57f)
                templatePath.lineTo(w * 0.82f, h * 0.8f)
            }
            "l" -> {
                templatePath.moveTo(w * 0.5f, h * 0.25f)
                templatePath.lineTo(w * 0.5f, h * 0.8f)
            }
            "m" -> {
                templatePath.moveTo(w * 0.15f, h * 0.4f)
                templatePath.lineTo(w * 0.15f, h * 0.8f)
                templatePath.moveTo(w * 0.15f, h * 0.4f)

                templatePath.arcTo(RectF(w * 0.15f, h * 0.42f, w * 0.5f, h * 0.8f), 180f, 180f, false)
                templatePath.lineTo(w * 0.5f, h * 0.8f)
                templatePath.moveTo(w * 0.5f, h * 0.8f)

                templatePath.arcTo(RectF(w * 0.5f, h * 0.42f, w * 0.88f, h * 0.8f), 180f, 180f, false)
                templatePath.lineTo(w * 0.88f, h * 0.8f)
            }
            "n" -> {
                templatePath.moveTo(w * 0.22f, h * 0.4f)
                templatePath.lineTo(w * 0.22f, h * 0.8f)
                templatePath.arcTo(RectF(w * 0.23f, h * 0.42f, w * 0.83f, h * 0.8f), 180f, 180f, false)
                templatePath.lineTo(w * 0.83f, h * 0.8f)
            }
            "o" -> {
                templatePath.addOval(w * 0.15f, h * 0.42f, w * 0.85f, h * 0.8f, Path.Direction.CW)
            }
            "p" -> {
                templatePath.moveTo(w * 0.2f, h * 0.4f)
                templatePath.lineTo(w * 0.2f, h * 0.98f)
                templatePath.moveTo(w * 0.2f, h * 0.5f)
                templatePath.addOval(w * 0.2f, h * 0.42f, w * 0.85f, h * 0.8f, Path.Direction.CW)
            }
            "q" -> {
                templatePath.moveTo(w * 0.8f, h * 0.4f)
                templatePath.lineTo(w * 0.8f, h * 0.98f)
                templatePath.moveTo(w * 0.8f, h * 0.5f)
                templatePath.addOval(w * 0.14f, h * 0.42f, w * 0.8f, h * 0.8f, Path.Direction.CW)
            }
            "r" -> {
                templatePath.moveTo(w * 0.32f, h * 0.4f)
                templatePath.lineTo(w * 0.32f, h * 0.8f)
                templatePath.moveTo(w * 0.32f, h * 0.65f)
                templatePath.quadTo(w * 0.5f, h * 0.4f, w * 0.85f, h * 0.42f)
            }
            "s" -> {
                templatePath.moveTo(w * 0.8f, h * 0.2f)
                templatePath.quadTo(w * 0.5f, h * 0.2f, w * 0.2f, h * 0.4f)
                templatePath.quadTo(w * 0.5f, h * 0.6f, w * 0.8f, h * 0.6f)
                templatePath.quadTo(w * 0.5f, h * 0.8f, w * 0.2f, h * 0.8f)
            }
            "t" -> {
                templatePath.moveTo(w * 0.2f, h * 0.2f)
                templatePath.lineTo(w * 0.8f, h * 0.2f)
                templatePath.moveTo(w * 0.5f, h * 0.2f)
                templatePath.lineTo(w * 0.5f, h * 0.8f)
            }
            "u" -> {
                templatePath.moveTo(w * 0.2f, h * 0.2f)
                templatePath.lineTo(w * 0.2f, h * 0.8f)
                templatePath.quadTo(w * 0.5f, h * 0.8f, w * 0.8f, h * 0.8f)
                templatePath.lineTo(w * 0.8f, h * 0.2f)
            }
            "v" -> {
                templatePath.moveTo(w * 0.14f, h * 0.4f)
                templatePath.lineTo(w * 0.5f, h * 0.8f)
                templatePath.lineTo(w * 0.86f, h * 0.4f)
            }
            "w" -> {
                templatePath.moveTo(w * 0.1f, h * 0.4f)
                templatePath.lineTo(w * 0.3f, h * 0.8f)
                templatePath.lineTo(w * 0.5f, h * 0.4f)
                templatePath.lineTo(w * 0.72f, h * 0.8f)
                templatePath.lineTo(w * 0.9f, h * 0.4f)
            }
            "x" -> {
                templatePath.moveTo(w * 0.2f, h * 0.4f)
                templatePath.lineTo(w * 0.8f, h * 0.8f)
                templatePath.moveTo(w * 0.8f, h * 0.4f)
                templatePath.lineTo(w * 0.2f, h * 0.8f)
            }
            "y" -> {
                templatePath.moveTo(w * 0.2f, h * 0.2f)
                templatePath.quadTo(w * 0.5f, h * 0.5f, w * 0.8f, h * 0.2f)
                templatePath.moveTo(w * 0.5f, h * 0.5f)
                templatePath.lineTo(w * 0.5f, h * 0.8f)
            }
            "z" -> {
                templatePath.moveTo(w * 0.2f, h * 0.43f)
                templatePath.lineTo(w * 0.8f, h * 0.43f)
                templatePath.lineTo(w * 0.2f, h * 0.8f)
                templatePath.lineTo(w * 0.8f, h * 0.8f)
            }
        }

        pathMeasure.setPath(templatePath, false)
        extractPathPoints()
    }

    private fun extractPathPoints() {
        val point = FloatArray(2)
        val step = pathMeasure.length / 180
        var distance = 0f

        templatePoints.clear()
        while (distance < pathMeasure.length) {
            pathMeasure.getPosTan(distance, point, null)
            templatePoints.add(PointF(point[0], point[1]))
            distance += step
        }
    }

    private fun isTracingCorrect(): Boolean {
        if (allUserPoints.size < templatePoints.size / 2) return false

        var matchCount = 0
        val tolerance = 10f

        for (userPoint in allUserPoints) {
            for (templatePoint in templatePoints) {
                val distance = euclideanDistance(userPoint, templatePoint)
                if (distance < tolerance) {
                    matchCount++
                    break
                }
            }
        }

        val matchPercentage = matchCount >= templatePoints.size * 0.98
        Log.d("TracingPercentage", "Matched: $matchCount / ${templatePoints.size} , ($matchPercentage)")
        return matchPercentage
    }

    private fun getDynamicTolerance(): Float {
        return viewWidth * 0.05f  // 5% dari lebar layar sebagai toleransi
    }

    private fun euclideanDistance(p1: PointF, p2: PointF): Float {
        return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isDrawing) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath.moveTo(event.x, event.y)
                userPath.add(currentPath)
                allUserPoints.add(PointF(event.x, event.y))
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(event.x, event.y)
                allUserPoints.add(PointF(event.x, event.y))
            }
            MotionEvent.ACTION_UP -> {
                if (allUserPoints.size >= templatePoints.size / 2) {
                    if (isTracingCorrect()) {
                        onCorrectTracing?.invoke()
                    }
                }
            }
        }
        invalidate()
        return true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        createTemplatePath(currentLetter, canvasWidth, canvasHeight)

        canvas.drawPath(templatePath, templatePaint)

        for (path in userPath) {
            canvas.drawPath(path, paint)
        }

        outlineBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), null)
        }

        if (isTracingCorrect()) {
            filledBitmap?.let { bitmap ->
                canvas.drawBitmap(bitmap, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), null)
            }
        }
    }
}
