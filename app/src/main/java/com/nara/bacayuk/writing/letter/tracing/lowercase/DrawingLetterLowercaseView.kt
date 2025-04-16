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

    private val userPaths = mutableListOf<Path>()
    private val userStrokes = mutableListOf<MutableList<PointF>>()
    private var currentStrokePoints = mutableListOf<PointF>()

    private val letterStrokes = mutableListOf<LetterStroke>()
    private var templatePath = Path()

    private var isDrawing = false
    private var onCorrectTracing: (() -> Unit)? = null
    private var currentLetter = "a"

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

    private var paint = pencil.apply { color = Color.BLUE }

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

        setLetter(currentLetter)
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

    fun setLetter(letter: String) {
        currentLetter = letter
        setLetterStrokes(letter)
        createTemplatePath()
        loadLetterDrawables(letter)
        clearCanvas()
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

    data class LetterStroke(
        val path: Path,
        val points: MutableList<PointF> = mutableListOf(),
        val isRequired: Boolean = true,
        var completed: Boolean = false
    )

    private fun setLetterStrokes(letter: String) {
        letterStrokes.clear()

        val w = viewWidth
        val h = viewHeight

        when (letter) {
            "a" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.5f)
                    quadTo(w * 0.8f, h * 0.2f, w * 0.8f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.78f, h * 0.6f)
                    quadTo(
                        w * 0.5f, h * 0.56f,
                        w * 0.2f, h * 0.68f
                    )
                    quadTo(
                        w * 0.2f, h * 0.9f,
                        w * 0.78f, h * 0.75f
                    )
                })
            }
            "b" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.22f)
                    lineTo(w * 0.2f, h * 0.8f)
                })

                val loop = Path()
                loop.moveTo(w * 0.2f, h * 0.5f)
                loop.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.85f, h * 0.8f), 180f, 180f, false)
                letterStrokes.add(LetterStroke(loop))
                extractPointsForStroke(letterStrokes.last())
            }
            "c" -> {
                val cPath = Path()
                cPath.moveTo(w * 0.8f, h * 0.5f)
                cPath.quadTo(w * 0.2f, h * 0.35f,
                    w * 0.18f, h * 0.55f)
                cPath.quadTo(w * 0.18f, h * 0.9f,
                    w * 0.8f, h * 0.75f)
                letterStrokes.add(LetterStroke(cPath))
                extractPointsForStroke(letterStrokes.last())
            }
            "d" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.8f, h * 0.2f)
                    lineTo(w * 0.8f, h * 0.8f)
                })

                val loop = Path()
                loop.moveTo(w * 0.8f, h * 0.5f)
                loop.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.8f, h * 0.8f), 0f, -180f, false)
                letterStrokes.add(LetterStroke(loop))
                extractPointsForStroke(letterStrokes.last())
            }
            "e" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.6f)
                    lineTo(w * 0.8f, h * 0.6f)
                })

                val curve = Path()
                curve.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.8f, h * 0.8f), 0f, 180f, false)
                letterStrokes.add(LetterStroke(curve))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.6f)
                    quadTo(w * 0.2f, h * 0.9f, w * 0.75f, h * 0.75f)
                })
            }
            "f" -> {
                val stem = Path()
                stem.moveTo(w * 0.85f, h * 0.25f)
                stem.quadTo(w * 0.6f, h * 0.2f, w * 0.45f, h * 0.4f)
                stem.lineTo(w * 0.45f, h * 0.8f)
                letterStrokes.add(LetterStroke(stem))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(w * 0.15f, h * 0.42f)
                    lineTo(w * 0.85f, h * 0.42f)
                })
            }
            "g" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.8f, h * 0.4f)
                    lineTo(w * 0.8f, h * 0.9f)
                })

                val oval = Path()
                oval.addOval(w * 0.16f, h * 0.4f, w * 0.8f, h * 0.75f, Path.Direction.CW)
                letterStrokes.add(LetterStroke(oval))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(w * 0.8f, h * 0.9f)
                    quadTo(w * 0.5f, h * 1f, w * 0.2f, h * 0.88f)
                })
            }
            "h" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.2f)
                    lineTo(w * 0.2f, h * 0.8f)
                })

                val arch = Path()
                arch.moveTo(w * 0.2f, h * 0.5f)
                arch.arcTo(RectF(w * 0.2f, h * 0.4f, w * 0.8f, h * 0.8f), 180f, 180f, false)
                letterStrokes.add(LetterStroke(arch))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(w * 0.8f, h * 0.5f)
                    lineTo(w * 0.8f, h * 0.8f)
                })
            }
            "i" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.5f, h * 0.4f)
                    lineTo(w * 0.5f, h * 0.8f)
                })

                val dot = Path()
                dot.addOval(w * 0.48f, h * 0.25f, w * 0.53f, h * 0.3f, Path.Direction.CW)
                letterStrokes.add(LetterStroke(dot))
                extractPointsForStroke(letterStrokes.last())
            }
            "j" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.62f, h * 0.4f)
                    lineTo(w * 0.62f, h * 0.83f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.62f, h * 0.83f)
                    quadTo(w * 0.62f, h * 0.98f, w * 0.15f, h * 0.95f)
                })

                val dot = Path()
                dot.addOval(w * 0.58f, h * 0.25f, w * 0.64f, h * 0.3f, Path.Direction.CW)
                letterStrokes.add(LetterStroke(dot))
                extractPointsForStroke(letterStrokes.last())
            }
            "k" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.26f)
                    lineTo(w * 0.2f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.7f)
                    lineTo(w * 0.83f, h * 0.38f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.46f, h * 0.57f)
                    lineTo(w * 0.82f, h * 0.8f)
                })
            }
            "l" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.5f, h * 0.25f)
                    lineTo(w * 0.5f, h * 0.8f)
                })
            }
            "m" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.15f, h * 0.4f)
                    lineTo(w * 0.15f, h * 0.8f)
                })

                val firstArch = Path()
                firstArch.moveTo(w * 0.15f, h * 0.4f)
                firstArch.arcTo(RectF(w * 0.15f, h * 0.42f, w * 0.5f, h * 0.8f), 180f, 180f, false)
                letterStrokes.add(LetterStroke(firstArch))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(w * 0.5f, h * 0.42f)
                    lineTo(w * 0.5f, h * 0.8f)
                })

                val secondArch = Path()
                secondArch.moveTo(w * 0.5f, h * 0.42f)
                secondArch.arcTo(RectF(w * 0.5f, h * 0.42f, w * 0.88f, h * 0.8f), 180f, 180f, false)
                letterStrokes.add(LetterStroke(secondArch))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(w * 0.88f, h * 0.42f)
                    lineTo(w * 0.88f, h * 0.8f)
                })
            }
            "n" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.22f, h * 0.4f)
                    lineTo(w * 0.22f, h * 0.8f)
                })

                val arch = Path()
                arch.moveTo(w * 0.22f, h * 0.4f)
                arch.arcTo(RectF(w * 0.23f, h * 0.42f, w * 0.83f, h * 0.8f), 180f, 180f, false)
                letterStrokes.add(LetterStroke(arch))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(w * 0.83f, h * 0.4f)
                    lineTo(w * 0.83f, h * 0.8f)
                })
            }
            "o" -> {
                val oval = Path()
                oval.addOval(w * 0.15f, h * 0.42f, w * 0.85f, h * 0.8f, Path.Direction.CW)
                letterStrokes.add(LetterStroke(oval))
                extractPointsForStroke(letterStrokes.last())
            }
            "p" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.4f)
                    lineTo(w * 0.2f, h * 0.98f)
                })

                val oval = Path()
                oval.addOval(w * 0.2f, h * 0.42f, w * 0.85f, h * 0.8f, Path.Direction.CW)
                letterStrokes.add(LetterStroke(oval))
                extractPointsForStroke(letterStrokes.last())
            }
            "q" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.8f, h * 0.4f)
                    lineTo(w * 0.8f, h * 0.98f)
                })

                val oval = Path()
                oval.addOval(w * 0.14f, h * 0.42f, w * 0.8f, h * 0.8f, Path.Direction.CW)
                letterStrokes.add(LetterStroke(oval))
                extractPointsForStroke(letterStrokes.last())
            }
            "r" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.32f, h * 0.4f)
                    lineTo(w * 0.32f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.32f, h * 0.65f)
                    quadTo(w * 0.5f, h * 0.4f, w * 0.85f, h * 0.42f)
                })
            }
            "s" -> {
                val sPath = Path()
                sPath.moveTo(w * 0.8f, h * 0.42f)
                sPath.quadTo(w * 0.5f, h * 0.2f, w * 0.2f, h * 0.4f)
                sPath.quadTo(w * 0.5f, h * 0.6f, w * 0.8f, h * 0.6f)
                sPath.quadTo(w * 0.5f, h * 0.8f, w * 0.2f, h * 0.8f)
                letterStrokes.add(LetterStroke(sPath))
                extractPointsForStroke(letterStrokes.last())
            }
            "t" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.5f, h * 0.2f)
                    lineTo(w * 0.5f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.4f)
                    lineTo(w * 0.8f, h * 0.4f)
                })
            }
            "u" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.4f)
                    lineTo(w * 0.2f, h * 0.7f)
                })

                val curve = Path()
                curve.moveTo(w * 0.2f, h * 0.7f)
                curve.quadTo(w * 0.5f, h * 0.8f, w * 0.8f, h * 0.7f)
                letterStrokes.add(LetterStroke(curve))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(w * 0.8f, h * 0.7f)
                    lineTo(w * 0.8f, h * 0.4f)
                })
            }
            "v" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.14f, h * 0.4f)
                    lineTo(w * 0.5f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.5f, h * 0.8f)
                    lineTo(w * 0.86f, h * 0.4f)
                })
            }
            "w" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.1f, h * 0.4f)
                    lineTo(w * 0.3f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.3f, h * 0.8f)
                    lineTo(w * 0.5f, h * 0.4f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.5f, h * 0.4f)
                    lineTo(w * 0.72f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.72f, h * 0.8f)
                    lineTo(w * 0.9f, h * 0.4f)
                })
            }
            "x" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.4f)
                    lineTo(w * 0.8f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.8f, h * 0.4f)
                    lineTo(w * 0.2f, h * 0.8f)
                })
            }
            "y" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.4f)
                    lineTo(w * 0.5f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.5f, h * 0.8f)
                    lineTo(w * 0.8f, h * 0.4f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.5f, h * 0.8f)
                    lineTo(w * 0.5f, h * 0.98f)
                })
            }
            "z" -> {
                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.43f)
                    lineTo(w * 0.8f, h * 0.43f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.8f, h * 0.43f)
                    lineTo(w * 0.2f, h * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(w * 0.2f, h * 0.8f)
                    lineTo(w * 0.8f, h * 0.8f)
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
                letterStrokes.add(LetterStroke(defaultPath))
                extractPointsForStroke(letterStrokes.last())
            }
        }
    }

    private fun createStroke(pathBuilder: Path.() -> Unit): LetterStroke {
        val strokePath = Path()
        strokePath.pathBuilder()
        val stroke = LetterStroke(strokePath)
        extractPointsForStroke(stroke)
        return stroke
    }

    private fun extractPointsForStroke(stroke: LetterStroke) {
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
        for (stroke in letterStrokes) {
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

        for (i in letterStrokes.indices) {
            if (completedStrokes.contains(i)) continue

            val letterStroke = letterStrokes[i]
            val progress = calculateStrokeProgress(lastStroke, letterStroke.points)
            strokesProgress[i] = progress

            if (progress >= 0.7f) {
                completedStrokes.add(i)
                letterStroke.completed = true
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

        for (i in letterStrokes.indices) {
            val stroke = letterStrokes[i]
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

        for (i in letterStrokes.indices) {
            if (completedStrokes.contains(i)) {
                canvas.drawPath(letterStrokes[i].path, completedStrokePaint)
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