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
import kotlin.math.pow
import kotlin.math.sqrt

class DrawingNumberView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val userPaths = mutableListOf<Path>()
    private val userStrokes = mutableListOf<MutableList<PointF>>()
    private var currentStrokePoints = mutableListOf<PointF>()

    private val numberStrokes = mutableListOf<NumberStroke>()
    private var templatePath = Path()

    private var isDrawing = false
    private var onCorrectTracing: (() -> Unit)? = null
    private var currentNumber = "0"

    private var outlineBitmap: Bitmap? = null
    private var filledBitmap: Bitmap? = null

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f

    private var completedStrokes = mutableSetOf<Int>()
    private var strokesProgress = mutableMapOf<Int, Float>()

    private var pencil = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private var eraser = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private var paint = pencil

    private val templatePaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 5f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val completedStrokePaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 8f
        alpha = 100
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        setNumber(currentNumber)
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
        userPaths.clear()
        userStrokes.clear()
        currentStrokePoints.clear()
        completedStrokes.clear()
        strokesProgress.clear()
        invalidate()
    }

    fun setOnCorrectTracingListener(listener: () -> Unit) {
        onCorrectTracing = listener
    }

    fun setNumber(number: String) {
        currentNumber = number
        setNumberStrokes(number)
        createTemplatePath()
        loadNumberDrawables(number)
        clearCanvas()
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

    data class NumberStroke(
        val path: Path,
        val points: MutableList<PointF> = mutableListOf(),
        val isRequired: Boolean = true,
        var completed: Boolean = false
    )

    private fun setNumberStrokes(number: String) {
        numberStrokes.clear()

        when (number) {
            "0" -> {
                val oval = Path()
                oval.addOval(
                    RectF(
                        viewWidth * 0.18f, viewHeight * 0.25f,
                        viewWidth * 0.82f, viewHeight * 0.8f
                    ),
                    Path.Direction.CW
                )
                numberStrokes.add(NumberStroke(oval))
                extractPointsForStroke(numberStrokes.last())
            }
            "1" -> {
                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.6f, viewHeight * 0.26f)
                })

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.6f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.6f, viewHeight * 0.8f)
                })
            }
            "2" -> {
                val topCurve = Path()
                topCurve.moveTo(viewWidth * 0.2f, viewHeight * 0.35f)
                topCurve.quadTo(
                    viewWidth * 0.6f, viewHeight * 0.15f,
                    viewWidth * 0.8f, viewHeight * 0.4f
                )
                numberStrokes.add(NumberStroke(topCurve))
                extractPointsForStroke(numberStrokes.last())

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.8f, viewHeight * 0.4f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.8f)
                })

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.8f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.8f)
                })
            }
            "3" -> {
                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.27f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.27f)
                })

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.8f, viewHeight * 0.27f)
                    lineTo(viewWidth * 0.44f, viewHeight * 0.5f)
                })

                val bottomCurve = Path()
                bottomCurve.moveTo(viewWidth * 0.44f, viewHeight * 0.5f)
                bottomCurve.quadTo(
                    viewWidth * 1.1f, viewHeight * 0.5f,
                    viewWidth * 0.65f, viewHeight * 0.8f
                )
                bottomCurve.quadTo(
                    viewWidth * 0.3f, viewHeight * 0.8f,
                    viewWidth * 0.2f, viewHeight * 0.7f
                )
                numberStrokes.add(NumberStroke(bottomCurve))
                extractPointsForStroke(numberStrokes.last())
            }
            "4" -> {
                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.69f, viewHeight * 0.25f)
                    lineTo(viewWidth * 0.69f, viewHeight * 0.8f)
                })

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.69f, viewHeight * 0.25f)
                    lineTo(viewWidth * 0.63f, viewHeight * 0.25f)
                })

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.63f, viewHeight * 0.25f)
                    lineTo(viewWidth * 0.12f, viewHeight * 0.62f)
                })

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.12f, viewHeight * 0.62f)
                    lineTo(viewWidth * 0.12f, viewHeight * 0.67f)
                })

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.12f, viewHeight * 0.67f)
                    lineTo(viewWidth * 0.86f, viewHeight * 0.67f)
                })
            }
            "5" -> {
                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.8f, viewHeight * 0.27f)
                    lineTo(viewWidth * 0.27f, viewHeight * 0.27f)
                })

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.27f, viewHeight * 0.27f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.55f)
                })

                val bottomCurve = Path()
                bottomCurve.moveTo(viewWidth * 0.2f, viewHeight * 0.55f)
                bottomCurve.quadTo(
                    viewWidth * 0.6f, viewHeight * 0.4f,
                    viewWidth * 0.8f, viewHeight * 0.6f
                )
                bottomCurve.quadTo(
                    viewWidth * 0.8f, viewHeight * 0.9f,
                    viewWidth * 0.2f, viewHeight * 0.73f
                )
                numberStrokes.add(NumberStroke(bottomCurve))
                extractPointsForStroke(numberStrokes.last())
            }
            "6" -> {
                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.20f, viewHeight * 0.6f)
                    lineTo(viewWidth * 0.62f, viewHeight * 0.24f)
                })

                val oval = Path()
                oval.addOval(
                    RectF(
                        viewWidth * 0.18f, viewHeight * 0.48f,
                        viewWidth * 0.82f, viewHeight * 0.8f
                    ),
                    Path.Direction.CW
                )
                numberStrokes.add(NumberStroke(oval))
                extractPointsForStroke(numberStrokes.last())
            }
            "7" -> {
                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.82f, viewHeight * 0.26f)
                })

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.82f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.3f, viewHeight * 0.8f)
                })
            }
            "8" -> {
                val topOval = Path()
                topOval.addOval(
                    RectF(
                        viewWidth * 0.2f, viewHeight * 0.25f,
                        viewWidth * 0.8f, viewHeight * 0.5f
                    ),
                    Path.Direction.CW
                )
                numberStrokes.add(NumberStroke(topOval))
                extractPointsForStroke(numberStrokes.last())

                val bottomOval = Path()
                bottomOval.addOval(
                    RectF(
                        viewWidth * 0.2f, viewHeight * 0.5f,
                        viewWidth * 0.8f, viewHeight * 0.8f
                    ),
                    Path.Direction.CW
                )
                numberStrokes.add(NumberStroke(bottomOval))
                extractPointsForStroke(numberStrokes.last())
            }
            "9" -> {
                val oval = Path()
                oval.addOval(
                    RectF(
                        viewWidth * 0.18f, viewHeight * 0.25f,
                        viewWidth * 0.82f, viewHeight * 0.58f
                    ),
                    Path.Direction.CW
                )
                numberStrokes.add(NumberStroke(oval))
                extractPointsForStroke(numberStrokes.last())

                numberStrokes.add(createStroke {
                    moveTo(viewWidth * 0.77f, viewHeight * 0.5f)
                    lineTo(viewWidth * 0.33f, viewHeight * 0.8f)
                })
            }
            else -> {
                val defaultPath = Path()
                defaultPath.addRect(
                    viewWidth * 0.2f,
                    viewHeight * 0.2f,
                    viewWidth * 0.8f,
                    viewHeight * 0.8f,
                    Path.Direction.CW
                )
                numberStrokes.add(NumberStroke(defaultPath))
                extractPointsForStroke(numberStrokes.last())
            }
        }
    }

    private fun createStroke(pathBuilder: Path.() -> Unit): NumberStroke {
        val strokePath = Path()
        strokePath.pathBuilder()
        val stroke = NumberStroke(strokePath)
        extractPointsForStroke(stroke)
        return stroke
    }

    private fun extractPointsForStroke(stroke: NumberStroke) {
        val pathMeasure = PathMeasure(stroke.path, false)
        val point = FloatArray(2)
        val step = 5f
        var distance = 0f

        while (distance <= pathMeasure.length) {
            pathMeasure.getPosTan(distance, point, null)
            stroke.points.add(PointF(point[0], point[1]))
            distance += step
        }
    }

    private fun createTemplatePath() {
        templatePath.reset()
        for (stroke in numberStrokes) {
            templatePath.addPath(stroke.path)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isDrawing) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val currentPath = Path()
                currentPath.moveTo(event.x, event.y)
                userPaths.add(currentPath)

                currentStrokePoints = mutableListOf()
                currentStrokePoints.add(PointF(event.x, event.y))
            }
            MotionEvent.ACTION_MOVE -> {
                userPaths.last().lineTo(event.x, event.y)
                currentStrokePoints.add(PointF(event.x, event.y))
            }
            MotionEvent.ACTION_UP -> {
                if (currentStrokePoints.isNotEmpty()) {
                    userStrokes.add(currentStrokePoints)
                    checkStrokeCompletion()

                    if (isAllRequiredStrokesCompleted()) {
                        onCorrectTracing?.invoke()
                    }
                }
            }
        }
        invalidate()
        return true
    }

    private fun checkStrokeCompletion() {
        val lastStroke = currentStrokePoints

        for (i in numberStrokes.indices) {
            if (completedStrokes.contains(i)) continue

            val numberStroke = numberStrokes[i]
            val progress = calculateStrokeProgress(lastStroke, numberStroke.points)
            strokesProgress[i] = progress

            if (progress >= 0.7f) {
                completedStrokes.add(i)
                numberStroke.completed = true
                Log.d("TracingDebug", "Stroke $i completed with progress $progress")
            }
        }
    }

    private fun calculateStrokeProgress(userStroke: List<PointF>, templatePoints: List<PointF>): Float {
        if (templatePoints.isEmpty()) return 0f

        var coveredPoints = 0
        val tolerance = getStrokeTolerance()

        for (templatePoint in templatePoints) {
            for (userPoint in userStroke) {
                if (euclideanDistance(templatePoint, userPoint) <= tolerance) {
                    coveredPoints++
                    break
                }
            }
        }

        return coveredPoints.toFloat() / templatePoints.size
    }

    private fun getStrokeTolerance(): Float {
        val diagonal = sqrt(viewWidth.pow(2) + viewHeight.pow(2))
        return diagonal * 0.04f
    }

    private fun euclideanDistance(p1: PointF, p2: PointF): Float {
        return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
    }

    private fun isAllRequiredStrokesCompleted(): Boolean {
        var requiredCount = 0
        var completedRequiredCount = 0

        for (i in numberStrokes.indices) {
            val stroke = numberStrokes[i]
            if (stroke.isRequired) {
                requiredCount++
                if (completedStrokes.contains(i)) {
                    completedRequiredCount++
                }
            }
        }

        Log.d("TracingDebug", "Completed $completedRequiredCount of $requiredCount required strokes")
        return completedRequiredCount == requiredCount && requiredCount > 0
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        outlineBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, null, RectF(0f, 0f, viewWidth, viewHeight), null)
        }

        canvas.drawPath(templatePath, templatePaint)

        for (i in numberStrokes.indices) {
            if (completedStrokes.contains(i)) {
                canvas.drawPath(numberStrokes[i].path, completedStrokePaint)
            }
        }

        for (path in userPaths) {
            canvas.drawPath(path, paint)
        }

        if (isAllRequiredStrokesCompleted()) {
            filledBitmap?.let { bitmap ->
                val alphaPaint = Paint().apply { alpha = 180 }
                canvas.drawBitmap(bitmap, null, RectF(0f, 0f, viewWidth, viewHeight), alphaPaint)
            }
        }
    }
}