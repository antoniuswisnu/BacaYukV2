package com.nara.bacayuk.writing.word.tracing

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
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withClip

class DrawingWordView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var userPath = mutableListOf<Path>()
    private var currentPath = Path()
    private val allUserPoints = mutableListOf<PointF>()
    private val templatePoints = mutableListOf<PointF>()

    private var paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val pencil = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
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

    private var isDrawing = true
    private var onCorrectTracing: (() -> Unit)? = null
    private var currentWord = ""
    private var currentLetterIndex = 0
    private var letterBoxes = mutableListOf<LetterBox>()
    private var isInitialized = false
    private var pathMeasure = PathMeasure()

    private data class LetterBox(
        val letter: Char,
        var rect: RectF,
        var isCompleted: Boolean = false,
        var outlineBitmap: Bitmap? = null,
        var filledBitmap: Bitmap? = null,
        var templatePath: Path = Path()
    )

    fun setDrawingMode(enabled: Boolean) {
        isDrawing = enabled
        paint = pencil
        invalidate()
    }

    fun setDrawingEraser(enabled: Boolean) {
        isDrawing = enabled
        paint = eraser
        invalidate()
    }

    fun clearCanvas() {
        userPath.clear()
        allUserPoints.clear()
        invalidate()
    }

    fun setOnCorrectTracingListener(listener: () -> Unit) {
        onCorrectTracing = listener
    }

    fun setWord(word: String) {
        currentWord = word.uppercase()
        currentLetterIndex = 0
        letterBoxes.clear()
        userPath.clear()
        allUserPoints.clear()
        isInitialized = false

        if (width > 0 && height > 0) {
            setupLetterBoxes()
            isInitialized = true
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && currentWord.isNotEmpty() && !isInitialized) {
            setupLetterBoxes()
            isInitialized = true
        }
    }

    private fun setupLetterBoxes() {
        if (width <= 0 || height <= 0) return

        val letterWidth = width / currentWord.length.toFloat()
        val letterHeight = height.toFloat()

        letterBoxes.clear()
        currentWord.forEachIndexed { index, letter ->
            val rect = RectF(
                index * letterWidth,
                0f,
                (index + 1) * letterWidth,
                letterHeight
            )

            try {
                val outlineBitmap = getBitmapForLetter(letter, false, letterWidth.toInt(), letterHeight.toInt())
                val filledBitmap = getBitmapForLetter(letter, true, letterWidth.toInt(), letterHeight.toInt())
                val templatePath = createTemplatePath(letter.toString(), rect)

                letterBoxes.add(
                    LetterBox(
                        letter = letter,
                        rect = rect,
                        outlineBitmap = outlineBitmap,
                        filledBitmap = filledBitmap,
                        templatePath = templatePath
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getBitmapForLetter(letter: Char, isFilled: Boolean, width: Int, height: Int): Bitmap? {
        if (width <= 0 || height <= 0) return null

        val resourceName = if (isFilled) {
            "huruf_${letter.lowercase()}_fill"
        } else {
            "huruf_${letter.lowercase()}_outline"
        }

        val resourceId = context.resources.getIdentifier(
            resourceName,
            "drawable",
            context.packageName
        )

        if (resourceId == 0) return null

        try {
            val drawable = ContextCompat.getDrawable(context, resourceId)
            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            drawable?.setBounds(0, 0, canvas.width, canvas.height)
            drawable?.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun createTemplatePath(letter: String, boundingRect: RectF): Path {
        val templatePath = Path()
        val width = boundingRect.width()
        val height = boundingRect.height()
        val offsetX = boundingRect.left
        val offsetY = boundingRect.top

        when (letter) {
            "A" -> {
                templatePath.moveTo(offsetX + width * 0.1f, offsetY + height * 0.8f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.25f)
                templatePath.lineTo(offsetX + width * 0.9f, offsetY + height * 0.8f)
                templatePath.moveTo(offsetX + width * 0.24f, offsetY + height * 0.65f)
                templatePath.lineTo(offsetX + width * 0.75f, offsetY + height * 0.65f)
            }
            "B" -> {
                templatePath.moveTo(offsetX + width * 0.19f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.19f, offsetY + height * 0.8f)
                templatePath.moveTo(offsetX + width * 0.19f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.6f, offsetY + height * 0.26f)
                templatePath.arcTo(
                    offsetX + width * 0.5f,
                    offsetY + height * 0.26f,
                    offsetX + width * 0.8f,
                    offsetY + height * 0.5f,
                    270f,
                    180f,
                    false
                )
                templatePath.lineTo(offsetX + width * 0.19f, offsetY + height * 0.5f)
                templatePath.moveTo(offsetX + width * 0.6f, offsetY + height * 0.5f)
                templatePath.arcTo(
                    offsetX + width * 0.5f,
                    offsetY + height * 0.5f,
                    offsetX + width * 0.8f,
                    offsetY + height * 0.8f,
                    270f,
                    180f,
                    false
                )
                templatePath.lineTo(offsetX + width * 0.19f, offsetY + height * 0.8f)
            }
            "C" -> {
                templatePath.moveTo(offsetX + width * 0.8f, offsetY + height * 0.3f)
                templatePath.quadTo(offsetX + width * 0.18f, offsetY + height * 0.2f, offsetX + width * 0.18f, offsetY + height * 0.5f)
                templatePath.quadTo(offsetX + width * 0.18f, offsetY + height * 0.9f, offsetX + width * 0.8f, offsetY + height * 0.75f)
            }
            "D" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.27f)
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.79f)
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.27f)
                templatePath.lineTo(offsetX + width * 0.3f, offsetY + height * 0.27f)
                templatePath.arcTo(
                    offsetX + width * 0.3f,
                    offsetY + height * 0.27f,
                    offsetX + width * 0.85f,
                    offsetY + height * 0.79f,
                    270f,
                    180f,
                    false
                )
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.79f)
            }
            "E" -> {
                templatePath.moveTo(offsetX + width * 0.25f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.25f, offsetY + height * 0.8f)
                templatePath.moveTo(offsetX + width * 0.25f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.85f, offsetY + height * 0.26f)
                templatePath.moveTo(offsetX + width * 0.25f, offsetY + height * 0.53f)
                templatePath.lineTo(offsetX + width * 0.85f, offsetY + height * 0.53f)
                templatePath.moveTo(offsetX + width * 0.25f, offsetY + height * 0.8f)
                templatePath.lineTo(offsetX + width * 0.85f, offsetY + height * 0.8f)
            }
            "F" -> {
                templatePath.moveTo(offsetX + width * 0.25f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.25f, offsetY + height * 0.8f)
                templatePath.moveTo(offsetX + width * 0.25f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.85f, offsetY + height * 0.26f)
                templatePath.moveTo(offsetX + width * 0.25f, offsetY + height * 0.53f)
                templatePath.lineTo(offsetX + width * 0.85f, offsetY + height * 0.53f)
            }
            "G" -> {
                templatePath.moveTo(offsetX + width * 0.8f, offsetY + height * 0.3f)
                templatePath.quadTo(offsetX + width * 0.18f, offsetY + height * 0.2f, offsetX + width * 0.18f, offsetY + height * 0.5f)
                templatePath.quadTo(offsetX + width * 0.18f, offsetY + height * 0.9f, offsetX + width * 0.8f, offsetY + height * 0.75f)
                templatePath.lineTo(offsetX + width * 0.83f, offsetY + height * 0.55f)
                templatePath.lineTo(offsetX + width * 0.55f, offsetY + height * 0.55f)
            }
            "H" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.8f)
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.53f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.53f)
                templatePath.moveTo(offsetX + width * 0.8f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.8f)
            }
            "I" -> {
                templatePath.moveTo(offsetX + width * 0.5f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.8f)
            }
            "J" -> {
                templatePath.moveTo(offsetX + width * 0.65f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.65f, offsetY + height * 0.7f)
                templatePath.quadTo(offsetX + width * 0.65f, offsetY + height * 0.78f, offsetX + width * 0.2f, offsetY + height * 0.78f)
            }
            "K" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.8f)
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.63f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.26f)
                templatePath.moveTo(offsetX + width * 0.46f, offsetY + height * 0.48f)
                templatePath.lineTo(offsetX + width * 0.82f, offsetY + height * 0.8f)
            }
            "L" -> {
                templatePath.moveTo(offsetX + width * 0.23f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.23f, offsetY + height * 0.79f)
                templatePath.moveTo(offsetX + width * 0.23f, offsetY + height * 0.79f)
                templatePath.lineTo(offsetX + width * 0.82f, offsetY + height * 0.79f)
            }
            "M" -> {
                templatePath.moveTo(offsetX + width * 0.15f, offsetY + height * 0.82f)
                templatePath.lineTo(offsetX + width * 0.15f, offsetY + height * 0.25f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.6f)
                templatePath.lineTo(offsetX + width * 0.84f, offsetY + height * 0.25f)
                templatePath.lineTo(offsetX + width * 0.84f, offsetY + height * 0.82f)
            }
            "N" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.8f)
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.25f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.8f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.25f)
            }
            "O" -> {
                templatePath.addOval(
                    offsetX + width * 0.12f,
                    offsetY + height * 0.25f,
                    offsetX + width * 0.88f,
                    offsetY + height * 0.8f,
                    Path.Direction.CW
                )
            }
            "P" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.8f)
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.6f, offsetY + height * 0.26f)
                templatePath.arcTo(
                    offsetX + width * 0.5f,
                    offsetY + height * 0.26f,
                    offsetX + width * 0.82f,
                    offsetY + height * 0.55f,
                    270f,
                    180f,
                    false
                )
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.55f)
            }
            "Q" -> {
                templatePath.addOval(
                    offsetX + width * 0.12f,
                    offsetY + height * 0.25f,
                    offsetX + width * 0.88f,
                    offsetY + height * 0.8f,
                    Path.Direction.CW
                )
                templatePath.moveTo(offsetX + width * 0.51f, offsetY + height * 0.61f)
                templatePath.lineTo(offsetX + width * 0.76f, offsetY + height * 0.9f)
            }
            "R" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.8f)
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.6f, offsetY + height * 0.26f)
                templatePath.arcTo(
                    offsetX + width * 0.5f,
                    offsetY + height * 0.26f,
                    offsetX + width * 0.8f,
                    offsetY + height * 0.55f,
                    270f,
                    180f,
                    false
                )
                templatePath.moveTo(offsetX + width * 0.65f, offsetY + height * 0.55f)
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.55f)
                templatePath.moveTo(offsetX + width * 0.53f, offsetY + height * 0.55f)
                templatePath.lineTo(offsetX + width * 0.82f, offsetY + height * 0.8f)
            }
            "S" -> {
                templatePath.moveTo(offsetX + width * 0.75f, offsetY + height * 0.33f)
                templatePath.cubicTo(
                    offsetX + width * 0.35f, offsetY + height * 0.1f,
                    offsetX + width * 0.1f, offsetY + height * 0.4f,
                    offsetX + width * 0.3f, offsetY + height * 0.5f
                )
                templatePath.cubicTo(
                    offsetX + width * 0.8f, offsetY + height * 0.6f,
                    offsetX + width * 1.1f, offsetY + height * 0.7f,
                    offsetX + width * 0.55f, offsetY + height * 0.8f
                )
                templatePath.quadTo(offsetX + width * 0.3f, offsetY + height * 0.85f, offsetX + width * 0.2f, offsetY + height * 0.7f)
            }
            "T" -> {
                templatePath.moveTo(offsetX + width * 0.1f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.9f, offsetY + height * 0.26f)
                templatePath.moveTo(offsetX + width * 0.5f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.8f)
            }
            "U" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.70f)
                templatePath.quadTo(offsetX + width * 0.5f, offsetY + height * 0.87f,
                    offsetX + width * 0.8f, offsetY + height * 0.70f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.26f)
            }
            "V" -> {
                templatePath.moveTo(offsetX + width * 0.16f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.8f)
                templatePath.lineTo(offsetX + width * 0.84f, offsetY + height * 0.26f)
            }
            "W" -> {
                templatePath.moveTo(offsetX + width * 0.1f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.3f, offsetY + height * 0.8f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.72f, offsetY + height * 0.8f)
                templatePath.lineTo(offsetX + width * 0.9f, offsetY + height * 0.26f)
            }
            "X" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.5f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.26f)
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.8f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.5f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.8f)
            }
            "Y" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.5f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.26f)
                templatePath.moveTo(offsetX + width * 0.5f, offsetY + height * 0.5f)
                templatePath.lineTo(offsetX + width * 0.5f, offsetY + height * 0.8f)
            }
            "Z" -> {
                templatePath.moveTo(offsetX + width * 0.2f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.26f)
                templatePath.lineTo(offsetX + width * 0.2f, offsetY + height * 0.8f)
                templatePath.lineTo(offsetX + width * 0.8f, offsetY + height * 0.8f)
            }
        }

        return templatePath
    }

    private fun extractPathPoints(path: Path) {
        templatePoints.clear()
        pathMeasure.setPath(path, false)
        val point = FloatArray(2)
        val step = pathMeasure.length / 100
        var distance = 0f

        while (distance < pathMeasure.length) {
            pathMeasure.getPosTan(distance, point, null)
            templatePoints.add(PointF(point[0], point[1]))
            distance += step
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isInitialized || letterBoxes.isEmpty() || currentLetterIndex >= letterBoxes.size) {
            return false
        }

        val currentLetterBox = letterBoxes[currentLetterIndex]

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isPointInLetterBox(event.x, event.y, currentLetterBox)) {
                    currentPath = Path()
                    currentPath.moveTo(event.x, event.y)
                    userPath.add(currentPath)
                    allUserPoints.add(PointF(event.x, event.y))
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isPointInLetterBox(event.x, event.y, currentLetterBox)) {
                    currentPath.lineTo(event.x, event.y)
                    allUserPoints.add(PointF(event.x, event.y))
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isPointInLetterBox(event.x, event.y, currentLetterBox)) {
                    extractPathPoints(currentLetterBox.templatePath)
                    if (isLetterTracedCorrectly()) {
                        currentLetterBox.isCompleted = true
                        currentLetterIndex++
                        userPath.clear()
                        allUserPoints.clear()

                        if (currentLetterIndex >= currentWord.length) {
                            onCorrectTracing?.invoke()
                        }
                    }
                    invalidate()
                    return true
                }
            }
        }
        return false
    }

    private fun isPointInLetterBox(x: Float, y: Float, letterBox: LetterBox): Boolean {
        return x >= letterBox.rect.left && x <= letterBox.rect.right &&
                y >= letterBox.rect.top && y <= letterBox.rect.bottom
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isInitialized || letterBoxes.isEmpty()) return

        letterBoxes.forEachIndexed { _, letterBox ->
            val bitmap = if (letterBox.isCompleted) {
                letterBox.filledBitmap
            } else {
                letterBox.outlineBitmap
            }

            bitmap?.let {
                canvas.drawBitmap(it, null, letterBox.rect, null)
            }
        }

        if (currentLetterIndex < letterBoxes.size) {
            val currentLetterBox = letterBoxes[currentLetterIndex]

             canvas.drawPath(currentLetterBox.templatePath, templatePaint)

            canvas.withClip(currentLetterBox.rect) {
                userPath.forEach { path ->
                    drawPath(path, paint)
                }
            }
        }
    }

    private fun isLetterTracedCorrectly(): Boolean {
        if (allUserPoints.size < templatePoints.size / 3) return false

        var matchCount = 0
        val tolerance = width * 0.01f

        for (templatePoint in templatePoints) {
            for (userPoint in allUserPoints) {
                val distance = euclideanDistance(userPoint, templatePoint)
                if (distance < tolerance) {
                    matchCount++
                    break
                }
            }
        }

        return matchCount >= templatePoints.size * 0.97f
    }

    private fun euclideanDistance(p1: PointF, p2: PointF): Float {
        return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
    }
}