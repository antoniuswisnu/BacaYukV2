package com.nara.bacayuk.writing.letter.tracing.capital

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

class DrawingLetterCapitalView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var userPath = mutableListOf<Path>()
    private val allUserPoints = mutableListOf<PointF>()
    private val templatePoints = mutableListOf<PointF>()

    private var pathMeasure = PathMeasure()
    private var currentPath = Path()
    private var templatePath = Path()

    private var isDrawing = false
    private var onCorrectTracing: (() -> Unit)? = null
    private var currentLetter = "A"

    private var outlineBitmap: Bitmap? = null
    private var filledBitmap: Bitmap? = null

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f

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

    private val eraser = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
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
        paint = pencil
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

        val outlineResourceName = "huruf_${letter.lowercase()}_outline"
        val filledResourceName = "huruf_${letter.lowercase()}_fill"

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

    private fun extractPathPoints() {
        val point = FloatArray(2)
        val step = pathMeasure.length / 100
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

        val width = width.toFloat()
        val height = height.toFloat()

        createTemplatePath(currentLetter, width, height)

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

    private fun createTemplatePath(letter: String, width: Float, height: Float) {
        templatePath.reset()

        when (letter) {
            "A" -> {
                templatePath.moveTo(width * 0.1f, height * 0.8f)
                templatePath.lineTo(width * 0.5f, height * 0.25f)
                templatePath.lineTo(width * 0.9f, height * 0.8f)
                templatePath.moveTo(width * 0.24f, height * 0.65f)
                templatePath.lineTo(width * 0.75f, height * 0.65f)
            }
            "B" -> {
                templatePath.moveTo(width * 0.19f, height * 0.26f)
                templatePath.lineTo(width * 0.19f, height * 0.8f)
                templatePath.moveTo(width * 0.19f, height * 0.26f)
                templatePath.lineTo(width * 0.6f, height * 0.26f)
                templatePath.arcTo(
                    width * 0.5f,
                    height * 0.26f,
                    width * 0.8f,
                    height * 0.5f,
                    270f,
                    180f,
                    false
                )
                templatePath.lineTo(width * 0.19f, height * 0.5f)
                templatePath.moveTo(width * 0.6f, height * 0.5f)
                templatePath.arcTo(
                    width * 0.5f,
                    height * 0.5f,
                    width * 0.8f,
                    height * 0.8f,
                    270f,
                    180f,
                    false
                )
                templatePath.lineTo(width * 0.19f, height * 0.8f)
            }
            "C" -> {
                templatePath.moveTo(width * 0.8f, height * 0.3f)
                templatePath.quadTo(width * 0.18f, height * 0.2f, width * 0.18f, height * 0.5f)
                templatePath.quadTo(width * 0.18f, height * 0.9f, width * 0.8f, height * 0.75f)
            }
            "D" -> {
                templatePath.moveTo(width * 0.2f, height * 0.27f)
                templatePath.lineTo(width * 0.2f, height * 0.79f)
                templatePath.moveTo(width * 0.2f, height * 0.27f)
                templatePath.lineTo(width * 0.3f, height * 0.27f)
                templatePath.arcTo(
                    width * 0.3f,
                    height * 0.27f,
                    width * 0.85f,
                    height * 0.79f,
                    270f,
                    180f,
                    false
                )
                templatePath.lineTo(width * 0.2f, height * 0.79f)
            }
            "E" -> {
                templatePath.moveTo(width * 0.25f, height * 0.26f)
                templatePath.lineTo(width * 0.25f, height * 0.8f)
                templatePath.moveTo(width * 0.25f, height * 0.26f)
                templatePath.lineTo(width * 0.85f, height * 0.26f)
                templatePath.moveTo(width * 0.25f, height * 0.53f)
                templatePath.lineTo(width * 0.85f, height * 0.53f)
                templatePath.moveTo(width * 0.25f, height * 0.8f)
                templatePath.lineTo(width * 0.85f, height * 0.8f)
            }
            "F" -> {
                templatePath.moveTo(width * 0.25f, height * 0.26f)
                templatePath.lineTo(width * 0.25f, height * 0.8f)
                templatePath.moveTo(width * 0.25f, height * 0.26f)
                templatePath.lineTo(width * 0.85f, height * 0.26f)
                templatePath.moveTo(width * 0.25f, height * 0.53f)
                templatePath.lineTo(width * 0.85f, height * 0.53f)
            }
            "G" -> {
                templatePath.moveTo(width * 0.8f, height * 0.3f)
                templatePath.quadTo(width * 0.18f, height * 0.2f, width * 0.18f, height * 0.5f)
                templatePath.quadTo(width * 0.18f, height * 0.9f, width * 0.8f, height * 0.75f)
                templatePath.moveTo(width * 0.5f, height * 0.5f)
                templatePath.lineTo(width * 0.8f, height * 0.5f)
            }
            "H" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.2f, height * 0.8f)
                templatePath.moveTo(width * 0.2f, height * 0.53f)
                templatePath.lineTo(width * 0.8f, height * 0.53f)
                templatePath.moveTo(width * 0.8f, height * 0.26f)
                templatePath.lineTo(width * 0.8f, height * 0.8f)
            }
            "I" -> {
                templatePath.moveTo(width * 0.5f, height * 0.26f)
                templatePath.lineTo(width * 0.5f, height * 0.8f)
//                templatePath.moveTo(w * 0.25f, h * 0.26f)
//                templatePath.lineTo(w * 0.75f, h * 0.26f)
//                templatePath.moveTo(w * 0.25f, h * 0.8f)
//                templatePath.lineTo(w * 0.75f, h * 0.8f)
            }
            "J" -> {
                templatePath.moveTo(width * 0.65f, height * 0.26f)
                templatePath.lineTo(width * 0.65f, height * 0.7f)
                templatePath.quadTo(width * 0.65f, height * 0.78f, width * 0.2f, height * 0.78f)
            }
            "K" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.2f, height * 0.8f)
                templatePath.moveTo(width * 0.2f, height * 0.63f)
                templatePath.lineTo(width * 0.8f, height * 0.26f)
                templatePath.moveTo(width * 0.46f, height * 0.48f)
                templatePath.lineTo(width * 0.82f, height * 0.8f)
            }
            "L" -> {
                templatePath.moveTo(width * 0.23f, height * 0.26f)
                templatePath.lineTo(width * 0.23f, height * 0.79f)
                templatePath.moveTo(width * 0.23f, height * 0.79f)
                templatePath.lineTo(width * 0.82f, height * 0.79f)
            }
            "M" -> {
                templatePath.moveTo(width * 0.15f, height * 0.82f)
                templatePath.lineTo(width * 0.15f, height * 0.25f)
                templatePath.lineTo(width * 0.5f, height * 0.6f)
                templatePath.lineTo(width * 0.84f, height * 0.25f)
                templatePath.lineTo(width * 0.84f, height * 0.82f)
            }
            "N" -> {
                templatePath.moveTo(width * 0.2f, height * 0.8f)
                templatePath.lineTo(width * 0.2f, height * 0.25f)
                templatePath.lineTo(width * 0.8f, height * 0.8f)
                templatePath.lineTo(width * 0.8f, height * 0.25f)
            }
            "O" -> {
                templatePath.addOval(
                    width * 0.12f,
                    height * 0.25f,
                    width * 0.88f,
                    height * 0.8f,
                    Path.Direction.CW
                )
            }
            "P" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.2f, height * 0.8f)
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.6f, height * 0.26f)
                templatePath.arcTo(
                    width * 0.5f,
                    height * 0.26f,
                    width * 0.82f,
                    height * 0.55f,
                    270f,
                    180f,
                    false
                )
                templatePath.lineTo(width * 0.2f, height * 0.55f)

            }
            "Q" -> {
                templatePath.addOval(
                    width * 0.12f,
                    height * 0.25f,
                    width * 0.88f,
                    height * 0.8f,
                    Path.Direction.CW
                )
                templatePath.moveTo(width * 0.51f, height * 0.61f)
                templatePath.lineTo(width * 0.76f, height * 0.9f)
            }
            "R" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.2f, height * 0.8f)
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.6f, height * 0.26f)
                templatePath.arcTo(
                    width * 0.5f,
                    height * 0.26f,
                    width * 0.8f,
                    height * 0.55f,
                    270f,
                    180f,
                    false
                )
                templatePath.moveTo(width * 0.65f, height * 0.55f)
                templatePath.lineTo(width * 0.2f, height * 0.55f)
                templatePath.moveTo(width * 0.53f, height * 0.55f)
                templatePath.lineTo(width * 0.82f, height * 0.8f)
            }
            "S" -> {
//                templatePath.moveTo(w * 0.78f, h * 0.36f) // Mulai dari kanan atas
//                templatePath.quadTo(w * 0.3f, h * 0.1f, w * 0.25f, h * 0.4f) // Lekukan atas ke kiri
//                templatePath.quadTo(w * 0.2f, h * 0.6f, w * 0.7f, h * 0.65f) // Lengkungan tengah
//                templatePath.quadTo(w * 0.8f, h * 0.7f, w * 0.7f, h * 0.85f) // Lengkungan bawah ke kanan
//                templatePath.quadTo(w * 0.6f, h * 0.9f, w * 0.3f, h * 0.85f) // Lekukan bawah ke kiri

                templatePath.moveTo(width * 0.75f, height * 0.33f)
                templatePath.cubicTo(
                    width * 0.35f, height * 0.1f,   // Kontrol kiri atas
                    width * 0.1f, height * 0.4f,   // Kontrol kiri bawah
                    width * 0.3f, height * 0.5f     // Titik tengah
                )
                templatePath.cubicTo(
                    width * 0.8f, height * 0.6f,   // Kontrol kanan atas
                    width * 1.1f, height * 0.7f,   // Kontrol kanan bawah
                    width * 0.55f, height * 0.8f   // Akhir di kiri bawah
                )
                templatePath.quadTo(width * 0.3f, height * 0.85f, width * 0.2f, height * 0.7f)
            }
            "T" -> {
                templatePath.moveTo(width * 0.1f, height * 0.26f)
                templatePath.lineTo(width * 0.9f, height * 0.26f)
                templatePath.moveTo(width * 0.5f, height * 0.26f)
                templatePath.lineTo(width * 0.5f, height * 0.8f)
            }
            "U" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.2f, height * 0.8f)
                templatePath.arcTo(
                    width * 0.2f,
                    height * 0.8f,
                    width * 0.8f,
                    height * 0.8f,
                    180f,
                    270f,
                    false
                )
                templatePath.lineTo(width * 0.8f, height * 0.26f)

                templatePath.moveTo(width * 0.2f, height * 0.2f)
                templatePath.quadTo(width * 0.5f, height * 0.8f, width * 0.8f, height * 0.2f)
            }
            "V" -> {
                templatePath.moveTo(width * 0.16f, height * 0.26f)
                templatePath.lineTo(width * 0.5f, height * 0.8f)
                templatePath.lineTo(width * 0.84f, height * 0.26f)
            }
            "W" -> {
                templatePath.moveTo(width * 0.1f, height * 0.26f)
                templatePath.lineTo(width * 0.3f, height * 0.8f)
                templatePath.lineTo(width * 0.5f, height * 0.26f)
                templatePath.lineTo(width * 0.72f, height * 0.8f)
                templatePath.lineTo(width * 0.9f, height * 0.26f)
            }
            "X" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.5f, height * 0.5f)
                templatePath.lineTo(width * 0.8f, height * 0.26f)
                templatePath.moveTo(width * 0.2f, height * 0.8f)
                templatePath.lineTo(width * 0.5f, height * 0.5f)
                templatePath.lineTo(width * 0.8f, height * 0.8f)
            }
            "Y" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.5f, height * 0.5f)
                templatePath.lineTo(width * 0.8f, height * 0.26f)
                templatePath.moveTo(width * 0.5f, height * 0.5f)
                templatePath.lineTo(width * 0.5f, height * 0.8f)
            }
            "Z" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.8f, height * 0.26f)
                templatePath.lineTo(width * 0.2f, height * 0.8f)
                templatePath.lineTo(width * 0.8f, height * 0.8f)
            }
        }

        pathMeasure.setPath(templatePath, false)
        extractPathPoints()
    }
}