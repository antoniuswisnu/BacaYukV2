package com.nara.bacayuk.writing.number.tracing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class DrawingNumberView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var userPath = mutableListOf<Path>()
    private val allUserPoints = mutableListOf<PointF>()
    private val templatePoints = mutableListOf<PointF>()

    private var pathMeasure = PathMeasure()
    private var currentPath = Path()
    private var templatePath = Path()

    private var isDrawing = false
    private var onCorrectTracing: (() -> Unit)? = null
    private var currentNumber = "0"

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
        loadNumberDrawables(currentNumber)
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

    fun setNumber(number: String) {
        currentNumber = number
        createTemplatePath(currentNumber, viewWidth, viewHeight)
        loadNumberDrawables(currentNumber)
        clearCanvas() // Reset user's drawing when number changes
        invalidate()
    }

    private fun loadNumberDrawables(number: String) {
        if (width <= 0 || height <= 0) return

        val outlineResourceName = "angka_${number}_outline"
        val filledResourceName = "angka_${number}_fill"

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
        val step = pathMeasure.length / 40
        var distance = 0f

        templatePoints.clear()
        while (distance < pathMeasure.length) {
            pathMeasure.getPosTan(distance, point, null)
            templatePoints.add(PointF(point[0], point[1]))
            distance += step
        }
    }

    private fun isTracingCorrect(): Boolean {
        if (allUserPoints.isEmpty() || templatePoints.isEmpty()) return false

        // Memerlukan minimal setengah dari jumlah titik template untuk diperiksa
        if (allUserPoints.size < templatePoints.size / 3) {
            Log.d("TracingCheck", "Not enough user points: ${allUserPoints.size} < ${templatePoints.size / 3}")
            return false
        }

        var matchCount = 0
        val tolerance = getDynamicTolerance()

        // Kita periksa apakah titik template berada di dekat jalur yang digambar pengguna
        for (templatePoint in templatePoints) {
            var isMatched = false
            for (userPoint in allUserPoints) {
                val distance = euclideanDistance(userPoint, templatePoint)
                if (distance < tolerance) {
                    isMatched = true
                    matchCount++
                    break
                }
            }
        }

        // Minimum 50% dari template points harus cocok dengan user points
        val matchPercentage = matchCount.toFloat() / templatePoints.size.toFloat()
        val isCorrect = matchPercentage >= 0.8f

        Log.d("TracingPercentage", "Matched: $matchCount / ${templatePoints.size} (${matchPercentage * 100}%), Tolerance: $tolerance, Result: $isCorrect")
        return isCorrect
    }

    private fun getDynamicTolerance(): Float {
        // Toleransi dinamis berdasarkan ukuran view (5% dari lebar layar)
        return viewWidth * 0.05f
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
                if (isTracingCorrect()) {
                    Log.d("TracingResult", "Tracing completed correctly!")
                    onCorrectTracing?.invoke()
                } else {
                    Log.d("TracingResult", "Tracing not correct yet.")
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

        createTemplatePath(currentNumber, width, height)

        canvas.drawPath(templatePath, templatePaint)

        for (path in userPath) {
            canvas.drawPath(path, paint)
        }

        outlineBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, null, RectF(0f, 0f, width, height), null)
        }

        // Hanya tampilkan bitmap filled jika tracing benar
        if (isTracingCorrect()) {
            filledBitmap?.let { bitmap ->
                canvas.drawBitmap(bitmap, null, RectF(0f, 0f, width, height), null)
            }
        }
    }

    private fun createTemplatePath(letter: String, width: Float, height: Float) {
        templatePath.reset()

        when (letter) {
            "0" -> {
                templatePath.addOval(
                    RectF(
                        width * 0.18f, height * 0.25f,
                        width * 0.82f, height * 0.8f
                    ),
                    Path.Direction.CW
                )
            }

            "1" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.6f, height * 0.26f)

                templatePath.moveTo(width * 0.6f, height * 0.26f)
                templatePath.lineTo(width * 0.6f, height * 0.8f)
            }

            "2" -> {
                templatePath.moveTo(width * 0.2f, height * 0.35f)
                templatePath.quadTo(
                    width * 0.6f, height * 0.15f,
                    width * 0.8f, height * 0.4f
                )

                templatePath.moveTo(width * 0.8f, height * 0.4f)
                templatePath.lineTo(width * 0.2f, height * 0.8f)

                templatePath.lineTo(width * 0.8f, height * 0.8f)
            }

            "3" -> {
                templatePath.moveTo(width * 0.2f, height * 0.27f)
                templatePath.lineTo(width * 0.8f, height * 0.27f)

                templatePath.moveTo(width * 0.8f, height * 0.27f)
                templatePath.lineTo(width * 0.44f, height * 0.5f)

                templatePath.moveTo(width * 0.44f, height * 0.5f)
                templatePath.quadTo(
                    width * 1.1f, height * 0.5f,
                    width * 0.65f, height * 0.8f
                )

                templatePath.quadTo(
                    width * 0.3f, height * 0.8f,
                    width * 0.2f, height * 0.7f
                )
            }

            "4" -> {
                templatePath.moveTo(width * 0.69f, height * 0.25f)
                templatePath.lineTo(width * 0.69f, height * 0.8f)

                templatePath.moveTo(width * 0.69f, height * 0.25f)
                templatePath.lineTo(width * 0.63f, height * 0.25f)

                templatePath.moveTo(width * 0.63f, height * 0.25f)
                templatePath.lineTo(width * 0.12f, height * 0.62f)

                templatePath.moveTo(width * 0.12f, height * 0.62f)
                templatePath.lineTo(width * 0.12f, height * 0.67f)

                templatePath.moveTo(width * 0.12f, height * 0.67f)
                templatePath.lineTo(width * 0.86f, height * 0.67f)
            }

            "5" -> {
                templatePath.moveTo(width * 0.8f, height * 0.27f)
                templatePath.lineTo(width * 0.27f, height * 0.27f)
                templatePath.lineTo(width * 0.2f, height * 0.55f)

                templatePath.moveTo(width * 0.2f, height * 0.55f)
                templatePath.quadTo(
                    width * 0.6f, height * 0.4f,
                    width * 0.8f, height * 0.6f
                )
                templatePath.quadTo(
                    width * 0.8f, height * 0.9f,
                    width * 0.2f, height * 0.73f
                )
            }

            "6" -> {
                templatePath.addOval(
                    RectF(
                        width * 0.18f, height * 0.48f,
                        width * 0.82f, height * 0.8f
                    ),
                    Path.Direction.CW
                )
                templatePath.moveTo(width * 0.20f, height * 0.6f)
                templatePath.lineTo(width * 0.62f, height * 0.24f)
            }

            "7" -> {
                templatePath.moveTo(width * 0.2f, height * 0.26f)
                templatePath.lineTo(width * 0.82f, height * 0.26f)

                templatePath.moveTo(width * 0.82f, height * 0.26f)
                templatePath.lineTo(width * 0.3f, height * 0.8f)
            }

            "8" -> {
                templatePath.addOval(
                    RectF(
                        width * 0.2f, height * 0.25f,
                        width * 0.8f, height * 0.5f
                    ),
                    Path.Direction.CW
                )
                templatePath.addOval(
                    RectF(
                        width * 0.2f, height * 0.5f,
                        width * 0.8f, height * 0.8f
                    ),
                    Path.Direction.CW
                )
            }

            "9" -> {
                templatePath.addOval(
                    RectF(
                        width * 0.18f, height * 0.25f,
                        width * 0.82f, height * 0.58f
                    ),
                    Path.Direction.CW
                )
                templatePath.moveTo(width * 0.77f, height * 0.5f)
                templatePath.lineTo(width * 0.33f, height * 0.8f)
            }
        }

        pathMeasure.setPath(templatePath, false)
        extractPathPoints()
    }
}