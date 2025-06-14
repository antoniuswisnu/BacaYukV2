package com.nara.bacayuk.writing.letter.tracing.capital

import  android.annotation.SuppressLint
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

    private val userPaths = mutableListOf<Path>()
    private val userStrokes = mutableListOf<MutableList<PointF>>()
    private var currentStrokePoints = mutableListOf<PointF>()

    private val letterStrokes = mutableListOf<LetterStroke>()
    private var templatePath = Path()

    private var isDrawing = false
    private var onCorrectTracing: (() -> Unit)? = null
    private var currentLetter = "A"

    private var drawingBitmap: Bitmap? = null
    private var drawingCanvas: Canvas? = null
    private val bitmapPaint = Paint(Paint.DITHER_FLAG)
    private var currentPath = Path()


    private var outlineBitmap: Bitmap? = null
    private var filledBitmap: Bitmap? = null

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f

    private var completedStrokes = mutableSetOf<Int>()
    private var strokesProgress = mutableMapOf<Int, Float>()

    private var pencil = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private var eraser = Paint().apply {
        isAntiAlias = true
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        style = Paint.Style.STROKE
        strokeWidth = 40f
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

        if (drawingBitmap == null) {
            drawingBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            drawingCanvas = Canvas(drawingBitmap!!)
        }

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
        drawingBitmap?.eraseColor(Color.TRANSPARENT)
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

    data class LetterStroke(
        val path: Path,
        val points: MutableList<PointF> = mutableListOf(),
        val isRequired: Boolean = true,
        var completed: Boolean = false
    )

    private fun setLetterStrokes(letter: String) {
        letterStrokes.clear()

        when (letter) {
            "A" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.1f, viewHeight * 0.8f)
                    lineTo(viewWidth * 0.5f, viewHeight * 0.25f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.5f, viewHeight * 0.25f)
                    lineTo(viewWidth * 0.9f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.24f, viewHeight * 0.65f)
                    lineTo(viewWidth * 0.75f, viewHeight * 0.65f)
                })
            }
            "B" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.19f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.19f, viewHeight * 0.8f)
                })

                val topCurve = Path()
                topCurve.moveTo(viewWidth * 0.19f, viewHeight * 0.26f)
                topCurve.lineTo(viewWidth * 0.6f, viewHeight * 0.26f)
                topCurve.arcTo(
                    viewWidth * 0.5f,
                    viewHeight * 0.26f,
                    viewWidth * 0.8f,
                    viewHeight * 0.5f,
                    270f,
                    180f,
                    false
                )
                topCurve.lineTo(viewWidth * 0.19f, viewHeight * 0.5f)
                letterStrokes.add(LetterStroke(topCurve))
                extractPointsForStroke(letterStrokes.last())

                val bottomCurve = Path()
                bottomCurve.moveTo(viewWidth * 0.19f, viewHeight * 0.5f)
                bottomCurve.lineTo(viewWidth * 0.6f, viewHeight * 0.5f)
                bottomCurve.arcTo(
                    viewWidth * 0.5f,
                    viewHeight * 0.5f,
                    viewWidth * 0.8f,
                    viewHeight * 0.8f,
                    270f,
                    180f,
                    false
                )
                bottomCurve.lineTo(viewWidth * 0.19f, viewHeight * 0.8f)
                letterStrokes.add(LetterStroke(bottomCurve))
                extractPointsForStroke(letterStrokes.last())
            }
            "C" -> {
                val cPath = Path()
                cPath.moveTo(viewWidth * 0.8f, viewHeight * 0.35f)
                cPath.quadTo(viewWidth * 0.29f, viewHeight * 0.1f, viewWidth * 0.18f, viewHeight * 0.5f)
                cPath.quadTo(viewWidth * 0.18f, viewHeight * 0.9f, viewWidth * 0.8f, viewHeight * 0.75f)
                letterStrokes.add(LetterStroke(cPath))
                extractPointsForStroke(letterStrokes.last())
            }
            "D" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.27f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.79f)
                })

                val curve = Path()
                curve.moveTo(viewWidth * 0.2f, viewHeight * 0.27f)
                curve.lineTo(viewWidth * 0.3f, viewHeight * 0.27f)
                curve.arcTo(
                    viewWidth * 0.3f,
                    viewHeight * 0.27f,
                    viewWidth * 0.85f,
                    viewHeight * 0.79f,
                    270f,
                    180f,
                    false
                )
                curve.lineTo(viewWidth * 0.2f, viewHeight * 0.79f)
                letterStrokes.add(LetterStroke(curve))
                extractPointsForStroke(letterStrokes.last())
            }
            "E" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.25f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.25f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.25f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.85f, viewHeight * 0.26f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.25f, viewHeight * 0.53f)
                    lineTo(viewWidth * 0.85f, viewHeight * 0.53f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.25f, viewHeight * 0.8f)
                    lineTo(viewWidth * 0.85f, viewHeight * 0.8f)
                })
            }
            "F" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.25f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.25f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.25f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.85f, viewHeight * 0.26f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.25f, viewHeight * 0.53f)
                    lineTo(viewWidth * 0.85f, viewHeight * 0.53f)
                })
            }
            "G" -> {
                val curve = Path()
                curve.moveTo(viewWidth * 0.8f, viewHeight * 0.35f)
                curve.quadTo(viewWidth * 0.5f, viewHeight * 0.2f, viewWidth * 0.25f, viewHeight * 0.3f)
                curve.quadTo(viewWidth * 0.15f, viewHeight * 0.4f, viewWidth * 0.15f, viewHeight * 0.55f)
                curve.quadTo(viewWidth * 0.15f, viewHeight * 0.7f, viewWidth * 0.35f, viewHeight * 0.8f)
                curve.quadTo(viewWidth * 0.8f, viewHeight * 0.85f, viewWidth * 0.85f, viewHeight * 0.55f)
                letterStrokes.add(LetterStroke(curve))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.85f, viewHeight * 0.55f)
                    lineTo(viewWidth * 0.57f, viewHeight * 0.55f)
                })
            }
            "H" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.53f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.53f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.8f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.8f)
                })
            }
            "I" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.5f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.5f, viewHeight * 0.8f)
                })
            }
            "J" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.65f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.65f, viewHeight * 0.7f)
                })

                val curve = Path()
                curve.moveTo(viewWidth * 0.65f, viewHeight * 0.7f)
                curve.quadTo(viewWidth * 0.65f, viewHeight * 0.78f, viewWidth * 0.2f, viewHeight * 0.78f)
                letterStrokes.add(LetterStroke(curve))
                extractPointsForStroke(letterStrokes.last())
            }
            "K" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.63f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.26f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.46f, viewHeight * 0.48f)
                    lineTo(viewWidth * 0.82f, viewHeight * 0.8f)
                })
            }
            "L" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.23f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.23f, viewHeight * 0.79f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.23f, viewHeight * 0.79f)
                    lineTo(viewWidth * 0.82f, viewHeight * 0.79f)
                })
            }
            "M" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.15f, viewHeight * 0.82f)
                    lineTo(viewWidth * 0.15f, viewHeight * 0.25f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.15f, viewHeight * 0.25f)
                    lineTo(viewWidth * 0.5f, viewHeight * 0.6f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.5f, viewHeight * 0.6f)
                    lineTo(viewWidth * 0.84f, viewHeight * 0.25f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.84f, viewHeight * 0.25f)
                    lineTo(viewWidth * 0.84f, viewHeight * 0.82f)
                })
            }
            "N" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.8f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.25f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.25f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.8f, viewHeight * 0.8f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.25f)
                })
            }
            "O" -> {
                val oval = Path()
                oval.addOval(
                    viewWidth * 0.12f,
                    viewHeight * 0.25f,
                    viewWidth * 0.88f,
                    viewHeight * 0.8f,
                    Path.Direction.CW
                )
                letterStrokes.add(LetterStroke(oval))
                extractPointsForStroke(letterStrokes.last())
            }
            "P" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.8f)
                })

                val loop = Path()
                loop.moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                loop.lineTo(viewWidth * 0.6f, viewHeight * 0.26f)
                loop.arcTo(
                    viewWidth * 0.5f,
                    viewHeight * 0.26f,
                    viewWidth * 0.82f,
                    viewHeight * 0.55f,
                    270f,
                    180f,
                    false
                )
                loop.lineTo(viewWidth * 0.2f, viewHeight * 0.55f)
                letterStrokes.add(LetterStroke(loop))
                extractPointsForStroke(letterStrokes.last())
            }
            "Q" -> {
                val circle = Path()
                circle.addOval(
                    viewWidth * 0.12f,
                    viewHeight * 0.25f,
                    viewWidth * 0.88f,
                    viewHeight * 0.8f,
                    Path.Direction.CW
                )
                letterStrokes.add(LetterStroke(circle))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.51f, viewHeight * 0.61f)
                    lineTo(viewWidth * 0.76f, viewHeight * 0.9f)
                })
            }
            "R" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.8f)
                })

                val loop = Path()
                loop.moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                loop.lineTo(viewWidth * 0.6f, viewHeight * 0.26f)
                loop.arcTo(
                    viewWidth * 0.5f,
                    viewHeight * 0.26f,
                    viewWidth * 0.8f,
                    viewHeight * 0.55f,
                    270f,
                    180f,
                    false
                )
                loop.lineTo(viewWidth * 0.2f, viewHeight * 0.55f)
                letterStrokes.add(LetterStroke(loop))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.53f, viewHeight * 0.55f)
                    lineTo(viewWidth * 0.82f, viewHeight * 0.8f)
                })
            }
            "S" -> {
                val sPath = Path()
                sPath.moveTo(viewWidth * 0.75f, viewHeight * 0.33f)
                sPath.cubicTo(
                    viewWidth * 0.35f, viewHeight * 0.1f,
                    viewWidth * 0.1f, viewHeight * 0.4f,
                    viewWidth * 0.3f, viewHeight * 0.5f
                )
                sPath.cubicTo(
                    viewWidth * 0.8f, viewHeight * 0.6f,
                    viewWidth * 1.1f, viewHeight * 0.7f,
                    viewWidth * 0.55f, viewHeight * 0.8f
                )
                sPath.quadTo(viewWidth * 0.3f, viewHeight * 0.85f, viewWidth * 0.2f, viewHeight * 0.7f)
                letterStrokes.add(LetterStroke(sPath))
                extractPointsForStroke(letterStrokes.last())
            }
            "T" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.1f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.9f, viewHeight * 0.26f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.5f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.5f, viewHeight * 0.8f)
                })
            }
            "U" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.7f)
                })

                val curve = Path()
                curve.moveTo(viewWidth * 0.2f, viewHeight * 0.6f)
                curve.arcTo(
                    viewWidth * 0.2f,
                    viewHeight * 0.6f,
                    viewWidth * 0.8f,
                    viewHeight * 0.8f,
                    180f,
                    -180f,
                    false
                )
                letterStrokes.add(LetterStroke(curve))
                extractPointsForStroke(letterStrokes.last())

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.8f, viewHeight * 0.7f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.26f)
                })
            }
            "V" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.16f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.5f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.5f, viewHeight * 0.8f)
                    lineTo(viewWidth * 0.84f, viewHeight * 0.26f)
                })
            }
            "W" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.1f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.3f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.3f, viewHeight * 0.8f)
                    lineTo(viewWidth * 0.5f, viewHeight * 0.26f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.5f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.72f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.72f, viewHeight * 0.8f)
                    lineTo(viewWidth * 0.9f, viewHeight * 0.26f)
                })
            }
            "X" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.8f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.8f)
                })
            }
            "Y" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.5f, viewHeight * 0.53f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.8f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.5f, viewHeight * 0.53f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.5f, viewHeight * 0.53f)
                    lineTo(viewWidth * 0.5f, viewHeight * 0.8f)
                })
            }
            "Z" -> {
                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.26f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.8f, viewHeight * 0.26f)
                    lineTo(viewWidth * 0.2f, viewHeight * 0.8f)
                })

                letterStrokes.add(createStroke {
                    moveTo(viewWidth * 0.2f, viewHeight * 0.8f)
                    lineTo(viewWidth * 0.8f, viewHeight * 0.8f)
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

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.reset()
                currentPath.moveTo(x, y)

                if (paint != eraser) {
                    currentStrokePoints = mutableListOf()
                    currentStrokePoints.add(PointF(event.x, event.y))
                }
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
                if (paint != eraser) {
                    currentStrokePoints.add(PointF(event.x, event.y))
                }
            }
            MotionEvent.ACTION_UP -> {
                drawingCanvas?.drawPath(currentPath, paint)
                currentPath.reset()

                if (paint != eraser && currentStrokePoints.isNotEmpty()) {
                    userStrokes.add(currentStrokePoints)
                    checkStrokeCompletion()

                    if (isAllRequiredStrokesCompleted()) {
                        onCorrectTracing?.invoke()
                    }
                }
            }
            else -> return false
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

        drawingBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, bitmapPaint)
        }

        canvas.drawPath(currentPath, paint)

        if (isAllRequiredStrokesCompleted()) {
            filledBitmap?.let { bitmap ->
                val alphaPaint = Paint().apply { alpha = 180 }
                canvas.drawBitmap(bitmap, null, RectF(0f, 0f, viewWidth, viewHeight), alphaPaint)
            }
        }
    }
}