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
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.min

class DrawingWordView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val userPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private val pencilPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 30f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private val templatePaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 8f
        pathEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)
        isAntiAlias = true
    }

    private val completedStrokePaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        alpha = 150
        isAntiAlias = true
    }

    private var currentWord = ""
    private var currentLetterIndex = 0
    private var letterBoxes = mutableListOf<LetterBox>()
    private var isInitialized = false
    private var activeDrawingPath = Path()

    private var onCorrectTracing: (() -> Unit)? = null
    private var onLetterCompleted: ((Char, Int) -> Unit)? = null

    private val TARGET_LETTER_ASPECT_RATIO = 0.75f
    private val STROKE_COMPLETION_THRESHOLD = 0.7f
    private val STROKE_TOLERANCE_FACTOR = 0.08f

    private data class DefinedStroke(
        val path: Path,
        val points: MutableList<PointF> = mutableListOf(),
        var isCompletedByPlayer: Boolean = false
    )

    private data class LetterBox(
        val letterChar: Char,
        var displayRect: RectF,
        var outlineBitmap: Bitmap? = null,
        var filledBitmap: Bitmap? = null,
        val definedStrokes: MutableList<DefinedStroke> = mutableListOf(),
        var userPathsForThisLetter: MutableList<Path> = mutableListOf(),
        var currentStrokeAttemptPoints: MutableList<PointF> = mutableListOf(),
        var completedStrokeIndicesThisLetter: MutableSet<Int> = mutableSetOf(),
        var isLetterFullyCompleted: Boolean = false
    )

    init {
        userPaint.set(pencilPaint)
    }

    fun setDrawingMode(isPencilMode: Boolean) {
        invalidate()
    }

    fun clearCanvas() {
        if (currentLetterIndex < letterBoxes.size) {
            val activeLetterBox = letterBoxes[currentLetterIndex]
            activeLetterBox.userPathsForThisLetter.clear()
            activeLetterBox.currentStrokeAttemptPoints.clear()
        }
        activeDrawingPath.reset()
        invalidate()
    }

    fun resetCurrentLetterProgress() {
        if (currentLetterIndex < letterBoxes.size) {
            val activeLetterBox = letterBoxes[currentLetterIndex]
            activeLetterBox.userPathsForThisLetter.clear()
            activeLetterBox.currentStrokeAttemptPoints.clear()
            activeLetterBox.completedStrokeIndicesThisLetter.clear()
            activeLetterBox.isLetterFullyCompleted = false
            activeLetterBox.definedStrokes.forEach { it.isCompletedByPlayer = false }
        }
        activeDrawingPath.reset()
        invalidate()
    }

    fun setOnCorrectTracingListener(listener: () -> Unit) {
        onCorrectTracing = listener
    }

    fun setOnLetterCompletedListener(listener: (Char, Int) -> Unit) {
        onLetterCompleted = listener
    }

    fun setWord(word: String) {
        currentWord = word
        currentLetterIndex = 0
        letterBoxes.clear()
        activeDrawingPath.reset()
        isInitialized = false

        if (width > 0 && height > 0) {
            setupLetterBoxesAndStrokes()
            isInitialized = true
        }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0 && currentWord.isNotEmpty()) {
            setupLetterBoxesAndStrokes()
            isInitialized = true
        }
    }

    private fun setupLetterBoxesAndStrokes() {
        if (width <= 0 || height <= 0 || currentWord.isEmpty()) return
        letterBoxes.clear()

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val idealLetterWidth = viewHeight * TARGET_LETTER_ASPECT_RATIO
        val totalIdealWordWidth = idealLetterWidth * currentWord.length

        val displayLetterWidth: Float
        val startX: Float

        if (totalIdealWordWidth <= viewWidth) {
            displayLetterWidth = idealLetterWidth
            startX = (viewWidth - totalIdealWordWidth) / 2f
        } else {
            displayLetterWidth = viewWidth / currentWord.length
            startX = 0f
        }

        val baseStrokeSize = displayLetterWidth * 0.1f
        userPaint.strokeWidth = baseStrokeSize
        pencilPaint.strokeWidth = baseStrokeSize
        templatePaint.strokeWidth = baseStrokeSize * 0.25f
        completedStrokePaint.strokeWidth = baseStrokeSize * 0.3f

        currentWord.forEachIndexed { index, char ->
            val letterLeft = startX + (index * displayLetterWidth)
            val rect = RectF(letterLeft, 0f, letterLeft + displayLetterWidth, viewHeight)
            val bitmapW = displayLetterWidth.toInt().coerceAtLeast(1)
            val bitmapH = viewHeight.toInt().coerceAtLeast(1)

            val newLetterBox = LetterBox(
                letterChar = char,
                displayRect = rect,
                outlineBitmap = getBitmapForLetter(char, false, bitmapW, bitmapH),
                filledBitmap = getBitmapForLetter(char, true, bitmapW, bitmapH)
            )
            defineStrokesForLetter(char, newLetterBox.definedStrokes, rect)
            letterBoxes.add(newLetterBox)
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getBitmapForLetter(letter: Char, isFilled: Boolean, targetWidth: Int, targetHeight: Int): Bitmap? {
        if (targetWidth <= 0 || targetHeight <= 0) return null

        val letterCharForResource = letter.lowercaseChar()
        val caseSpecificSuffix = if (letter.isLowerCase()) "_lowercase" else ""
        val fillType = if (isFilled) "fill" else "outline"
        val resourceName = "huruf_${letterCharForResource}${caseSpecificSuffix}_${fillType}"

        Log.d("DrawingWordView", "Attempting to load drawable: $resourceName for letter '$letter'")

        val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resourceId == 0) {
            Log.w("DrawingWordView", "Drawable resource not found: $resourceName. Falling back to placeholder.")
            return createPlaceholderBitmap(letter, targetWidth, targetHeight)
        }
        return try {
            ContextCompat.getDrawable(context, resourceId)?.toBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        } catch (e: Exception) {
            Log.e("DrawingWordView", "Error loading bitmap $resourceName: ${e.message}", e)
            createPlaceholderBitmap(letter, targetWidth, targetHeight)
        }
    }

    private fun createPlaceholderBitmap(letter: Char, width: Int, height: Int): Bitmap {
        val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { color = Color.DKGRAY; textSize = min(width, height) * 0.8f; textAlign = Paint.Align.CENTER; isAntiAlias = true }
        canvas.drawText(letter.toString(), width / 2f, height / 2f - (paint.descent() + paint.ascent()) / 2f, paint)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f; paint.color = Color.GRAY
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    private fun defineStrokesForLetter(letter: Char, strokesList: MutableList<DefinedStroke>, letterRect: RectF) {
        strokesList.clear()
        val w = letterRect.width()
        val h = letterRect.height()
        val ox = letterRect.left
        val oy = letterRect.top

        if (w <= 0 || h <= 0) return

        fun addStroke(isRequired: Boolean = true, pathBuilder: Path.() -> Unit) {
            val path = Path()
            path.pathBuilder()
            val definedStroke = DefinedStroke(path = path)
            extractPointsForDefinedStroke(definedStroke)
            strokesList.add(definedStroke)
        }

        when (letter) {
            'A' -> {
                addStroke {
                    moveTo(ox + w * 0.1f, oy + h * 0.8f)
                    lineTo(ox + w * 0.5f, oy + h * 0.25f)
                }
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.25f)
                    lineTo(ox + w * 0.9f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.24f, oy + h * 0.65f)
                    lineTo(ox + w * 0.75f, oy + h * 0.65f)
                }
            }
            'B' -> {
                addStroke {
                    moveTo(ox + w * 0.19f, oy + h * 0.26f)
                    lineTo(ox + w * 0.19f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.19f, oy + h * 0.26f)
                    lineTo(ox + w * 0.6f, oy + h * 0.26f)
                }
                addStroke {
                    moveTo(ox + w * 0.6f, oy + h * 0.26f)
                    arcTo(
                        ox + w * 0.5f, oy + h * 0.26f,
                        ox + w * 0.8f, oy + h * 0.5f,
                        270f, 180f, false
                    )
                    lineTo(ox + w * 0.19f, oy + h * 0.5f)
                }
                addStroke {
                    moveTo(ox + w * 0.19f, oy + h * 0.5f)
                    lineTo(ox + w * 0.6f, oy + h * 0.5f)
                }
                addStroke {
                    moveTo(ox + w * 0.6f, oy + h * 0.5f)
                    arcTo(
                        ox + w * 0.5f, oy + h * 0.5f,
                        ox + w * 0.8f, oy + h * 0.8f,
                        270f, 180f, false
                    )
                    lineTo(ox + w * 0.19f, oy + h * 0.8f)
                }
            }
            'C' -> {
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.3f)
                    quadTo(ox + w * 0.18f, oy + h * 0.2f, ox + w * 0.18f, oy + h * 0.5f)
                    quadTo(ox + w * 0.18f, oy + h * 0.9f, ox + w * 0.8f, oy + h * 0.75f)
                }
            }
            'D' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.27f)
                    lineTo(ox + w * 0.2f, oy + h * 0.79f)
                }
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.27f)
                    lineTo(ox + w * 0.3f, oy + h * 0.27f)
                }
                addStroke {
                    moveTo(ox + w * 0.3f, oy + h * 0.27f)
                    arcTo(
                        ox + w * 0.3f, oy + h * 0.27f,
                        ox + w * 0.85f, oy + h * 0.79f,
                        270f, 180f, false
                    )
                    lineTo(ox + w * 0.2f, oy + h * 0.79f)
                }
            }
            'E' -> {
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.26f)
                    lineTo(ox + w * 0.25f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.26f)
                    lineTo(ox + w * 0.85f, oy + h * 0.26f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.53f)
                    lineTo(ox + w * 0.85f, oy + h * 0.53f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.8f)
                    lineTo(ox + w * 0.85f, oy + h * 0.8f)
                }
            }
            'F' -> {
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.26f)
                    lineTo(ox + w * 0.25f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.26f)
                    lineTo(ox + w * 0.85f, oy + h * 0.26f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.53f)
                    lineTo(ox + w * 0.85f, oy + h * 0.53f)
                }
            }
            'G' -> {
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.3f)
                    quadTo(ox + w * 0.18f, oy + h * 0.2f, ox + w * 0.18f, oy + h * 0.5f)
                    quadTo(ox + w * 0.18f, oy + h * 0.9f, ox + w * 0.8f, oy + h * 0.75f)
                }
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.75f)
                    lineTo(ox + w * 0.83f, oy + h * 0.55f)
                }
                addStroke {
                    moveTo(ox + w * 0.83f, oy + h * 0.55f)
                    lineTo(ox + w * 0.55f, oy + h * 0.55f)
                }
            }
            'H' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.2f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.53f)
                    lineTo(ox + w * 0.8f, oy + h * 0.53f)
                }
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.26f)
                    lineTo(ox + w * 0.8f, oy + h * 0.8f)
                }
            }
            'I' -> {
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.26f)
                    lineTo(ox + w * 0.5f, oy + h * 0.8f)
                }
            }
            'J' -> {
                addStroke {
                    moveTo(ox + w * 0.65f, oy + h * 0.26f)
                    lineTo(ox + w * 0.65f, oy + h * 0.7f)
                }
                addStroke {
                    moveTo(ox + w * 0.65f, oy + h * 0.7f)
                    quadTo(ox + w * 0.65f, oy + h * 0.78f, ox + w * 0.2f, oy + h * 0.78f)
                }
            }
            'K' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.2f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.63f)
                    lineTo(ox + w * 0.8f, oy + h * 0.26f)
                }
                addStroke {
                    moveTo(ox + w * 0.46f, oy + h * 0.48f)
                    lineTo(ox + w * 0.82f, oy + h * 0.8f)
                }
            }
            'L' -> {
                addStroke {
                    moveTo(ox + w * 0.23f, oy + h * 0.26f)
                    lineTo(ox + w * 0.23f, oy + h * 0.79f)
                }
                addStroke {
                    moveTo(ox + w * 0.23f, oy + h * 0.79f)
                    lineTo(ox + w * 0.82f, oy + h * 0.79f)
                }
            }
            'M' -> {
                addStroke {
                    moveTo(ox + w * 0.15f, oy + h * 0.82f)
                    lineTo(ox + w * 0.15f, oy + h * 0.25f)
                }
                addStroke {
                    moveTo(ox + w * 0.15f, oy + h * 0.25f)
                    lineTo(ox + w * 0.5f, oy + h * 0.6f)
                }
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.6f)
                    lineTo(ox + w * 0.84f, oy + h * 0.25f)
                }
                addStroke {
                    moveTo(ox + w * 0.84f, oy + h * 0.25f)
                    lineTo(ox + w * 0.84f, oy + h * 0.82f)
                }
            }
            'N' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.8f)
                    lineTo(ox + w * 0.2f, oy + h * 0.25f)
                }
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.25f)
                    lineTo(ox + w * 0.8f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.8f)
                    lineTo(ox + w * 0.8f, oy + h * 0.25f)
                }
            }
            'O' -> {
                addStroke {
                    addOval(
                        ox + w * 0.12f, oy + h * 0.25f,
                        ox + w * 0.88f, oy + h * 0.8f,
                        Path.Direction.CW
                    )
                }
            }
            'P' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.2f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.6f, oy + h * 0.26f)
                }
                addStroke {
                    moveTo(ox + w * 0.6f, oy + h * 0.26f)
                    arcTo(
                        ox + w * 0.5f, oy + h * 0.26f,
                        ox + w * 0.82f, oy + h * 0.55f,
                        270f, 180f, false
                    )
                    lineTo(ox + w * 0.2f, oy + h * 0.55f)
                }
            }
            'Q' -> {
                addStroke {
                    addOval(
                        ox + w * 0.12f, oy + h * 0.25f,
                        ox + w * 0.88f, oy + h * 0.8f,
                        Path.Direction.CW
                    )
                }
                addStroke {
                    moveTo(ox + w * 0.51f, oy + h * 0.61f)
                    lineTo(ox + w * 0.76f, oy + h * 0.9f)
                }
            }
            'R' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.2f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.6f, oy + h * 0.26f)
                }
                addStroke {
                    moveTo(ox + w * 0.6f, oy + h * 0.26f)
                    arcTo(
                        ox + w * 0.5f, oy + h * 0.26f,
                        ox + w * 0.8f, oy + h * 0.55f,
                        270f, 180f, false
                    )
                    lineTo(ox + w * 0.2f, oy + h * 0.55f)
                }
                addStroke {
                    moveTo(ox + w * 0.53f, oy + h * 0.55f)
                    lineTo(ox + w * 0.82f, oy + h * 0.8f)
                }
            }
            'S' -> {
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.33f)
                    cubicTo(
                        ox + w * 0.35f, oy + h * 0.1f,
                        ox + w * 0.1f, oy + h * 0.4f,
                        ox + w * 0.3f, oy + h * 0.5f
                    )
                    cubicTo(
                        ox + w * 0.8f, oy + h * 0.6f,
                        ox + w * 1.1f, oy + h * 0.7f,
                        ox + w * 0.55f, oy + h * 0.8f
                    )
                    quadTo(ox + w * 0.3f, oy + h * 0.85f, ox + w * 0.2f, oy + h * 0.7f)
                }
            }
            'T' -> {
                addStroke {
                    moveTo(ox + w * 0.1f, oy + h * 0.26f)
                    lineTo(ox + w * 0.9f, oy + h * 0.26f)
                }
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.26f)
                    lineTo(ox + w * 0.5f, oy + h * 0.8f)
                }
            }
            'U' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.2f, oy + h * 0.70f)
                }
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.70f)
                    quadTo(ox + w * 0.5f, oy + h * 0.87f, ox + w * 0.8f, oy + h * 0.70f)
                }
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.70f)
                    lineTo(ox + w * 0.8f, oy + h * 0.26f)
                }
            }
            'V' -> {
                addStroke {
                    moveTo(ox + w * 0.16f, oy + h * 0.26f)
                    lineTo(ox + w * 0.5f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.8f)
                    lineTo(ox + w * 0.84f, oy + h * 0.26f)
                }
            }
            'W' -> {
                addStroke {
                    moveTo(ox + w * 0.1f, oy + h * 0.26f)
                    lineTo(ox + w * 0.3f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.3f, oy + h * 0.8f)
                    lineTo(ox + w * 0.5f, oy + h * 0.26f)
                }
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.26f)
                    lineTo(ox + w * 0.72f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.72f, oy + h * 0.8f)
                    lineTo(ox + w * 0.9f, oy + h * 0.26f)
                }
            }
            'X' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.8f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.26f)
                    lineTo(ox + w * 0.2f, oy + h * 0.8f)
                }
            }
            'Y' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.5f, oy + h * 0.5f)
                }
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.26f)
                    lineTo(ox + w * 0.5f, oy + h * 0.5f)
                }
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.5f)
                    lineTo(ox + w * 0.5f, oy + h * 0.8f)
                }
            }
            'Z' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.26f)
                    lineTo(ox + w * 0.8f, oy + h * 0.26f)
                }
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.26f)
                    lineTo(ox + w * 0.2f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.8f)
                    lineTo(ox + w * 0.8f, oy + h * 0.8f)
                }
            }

            // Lowercase

            'a' -> {
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.55f)
                    arcTo(RectF(ox + w * 0.25f, oy + h * 0.45f, ox + w * 0.75f, oy + h * 0.75f), 0f, -359.9f)
                }
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.5f)
                    lineTo(ox + w * 0.75f, oy + h * 0.75f)
                }
            }
            'b' -> {
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.2f)
                    lineTo(ox + w * 0.25f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.5f)
                    arcTo(RectF(ox + w * 0.25f, oy + h * 0.45f, ox + w * 0.75f, oy + h * 0.75f), 180f, -359.9f)
                }
            }
            'c' -> {
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.5f)
                    quadTo(ox + w * 0.3f, oy + h * 0.35f, ox + w * 0.25f, oy + h * 0.55f)
                    quadTo(ox + w * 0.3f, oy + h * 0.85f, ox + w * 0.8f, oy + h * 0.7f)
                }
            }
            'd' -> {
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.55f)
                    arcTo(RectF(ox + w * 0.25f, oy + h * 0.45f, ox + w * 0.75f, oy + h * 0.75f), 0f, 359.9f)
                }
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.2f)
                    lineTo(ox + w * 0.75f, oy + h * 0.8f)
                }
            }
            'e' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.6f)
                    lineTo(ox + w * 0.8f, oy + h * 0.6f)
                }
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.6f)
                    arcTo(RectF(ox + w * 0.2f, oy + h * 0.4f, ox + w * 0.8f, oy + h * 0.8f), 0f, 270f)
                }
            }
            'f' -> {
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.25f)
                    quadTo(ox + w * 0.5f, oy + h * 0.15f, ox + w * 0.4f, oy + h * 0.3f)
                    lineTo(ox + w * 0.4f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.45f)
                    lineTo(ox + w * 0.6f, oy + h * 0.45f)
                }
            }
            'g' -> { //
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.55f)
                    arcTo(RectF(ox + w * 0.25f, oy + h * 0.45f, ox + w * 0.75f, oy + h * 0.75f), 0f, -359.9f)
                }
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.5f)
                    lineTo(ox + w * 0.75f, oy + h * 0.9f)
                    quadTo(ox + w * 0.6f, oy + h * 1.0f, ox + w * 0.3f, oy + h * 0.9f)
                }
            }
            'h' -> {
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.2f)
                    lineTo(ox + w * 0.25f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.5f)
                    arcTo(RectF(ox + w * 0.25f, oy + h * 0.45f, ox + w * 0.75f, oy + h * 0.75f), 180f, 180f)
                    lineTo(ox + w * 0.75f, oy + h * 0.8f)
                }
            }
            'i' -> {
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.45f)
                    lineTo(ox + w * 0.5f, oy + h * 0.75f)
                }
                addStroke {
                    addOval(RectF(ox + w * 0.47f, oy + h * 0.3f, ox + w * 0.53f, oy + h * 0.38f), Path.Direction.CW)
                }
            }
            'j' -> {
                addStroke {
                    moveTo(ox + w * 0.55f, oy + h * 0.45f)
                    lineTo(ox + w * 0.55f, oy + h * 0.9f)
                    quadTo(ox + w * 0.5f, oy + h * 1.0f, ox + w * 0.2f, oy + h * 0.9f)
                }
                addStroke {
                    addOval(RectF(ox + w * 0.52f, oy + h * 0.3f, ox + w * 0.58f, oy + h * 0.38f), Path.Direction.CW)
                }
            }
            'k' -> {
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.2f)
                    lineTo(ox + w * 0.25f, oy + h * 0.8f)
                }
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.45f)
                    lineTo(ox + w * 0.25f, oy + h * 0.6f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.6f)
                    lineTo(ox + w * 0.75f, oy + h * 0.8f)
                }
            }
            'l' -> {
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.2f)
                    lineTo(ox + w * 0.5f, oy + h * 0.8f)
                }
            }
            'm' -> {
                addStroke {
                    moveTo(ox + w * 0.15f, oy + h * 0.45f)
                    lineTo(ox + w * 0.15f, oy + h * 0.75f)
                }
                addStroke {
                    moveTo(ox + w * 0.15f, oy + h * 0.5f)
                    arcTo(RectF(ox + w * 0.15f, oy + h * 0.45f, ox + w * 0.45f, oy + h * 0.75f), 180f, 180f)
                    lineTo(ox + w * 0.45f, oy + h * 0.75f)
                }
                addStroke {
                    moveTo(ox + w * 0.45f, oy + h * 0.5f)
                    arcTo(RectF(ox + w * 0.45f, oy + h * 0.45f, ox + w * 0.75f, oy + h * 0.75f), 180f, 180f)
                    lineTo(ox + w * 0.75f, oy + h * 0.75f)
                }
            }
            'n' -> { //
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.45f)
                    lineTo(ox + w * 0.25f, oy + h * 0.75f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.5f)
                    arcTo(RectF(ox + w * 0.25f, oy + h * 0.45f, ox + w * 0.75f, oy + h * 0.75f), 180f, 180f)
                    lineTo(ox + w * 0.75f, oy + h * 0.75f)
                }
            }
            'o' -> {
                addStroke {
                    addOval(RectF(ox + w * 0.2f, oy + h * 0.45f, ox + w * 0.8f, oy + h * 0.75f), Path.Direction.CW)
                }
            }
            'p' -> {
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.55f)
                    arcTo(RectF(ox + w * 0.25f, oy + h * 0.45f, ox + w * 0.75f, oy + h * 0.75f), 0f, 359.9f)
                }
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.5f)
                    lineTo(ox + w * 0.25f, oy + h * 0.95f)
                }
            }
            'q' -> {
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.55f)
                    arcTo(RectF(ox + w * 0.25f, oy + h * 0.45f, ox + w * 0.75f, oy + h * 0.75f), 0f, -359.9f)
                }
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.5f)
                    lineTo(ox + w * 0.75f, oy + h * 0.95f)
                }
            }
            'r' -> {
                addStroke {
                    moveTo(ox + w * 0.3f, oy + h * 0.45f)
                    lineTo(ox + w * 0.3f, oy + h * 0.75f)
                }
                addStroke {
                    moveTo(ox + w * 0.3f, oy + h * 0.5f)
                    quadTo(ox + w * 0.4f, oy + h * 0.4f, ox + w * 0.7f, oy + h * 0.45f)
                }
            }
            's' -> {
                addStroke {
                    moveTo(ox + w * 0.75f, oy + h * 0.5f)
                    quadTo(ox + w * 0.6f, oy + h * 0.4f, ox + w * 0.5f, oy + h * 0.45f)
                    quadTo(ox + w * 0.2f, oy + h * 0.55f, ox + w * 0.5f, oy + h * 0.65f)
                    quadTo(ox + w * 0.65f, oy + h * 0.7f, ox + w * 0.25f, oy + h * 0.7f)
                }
            }
            't' -> {
                addStroke {
                    moveTo(ox + w * 0.5f, oy + h * 0.25f)
                    lineTo(ox + w * 0.5f, oy + h * 0.75f)
                }
                addStroke {
                    moveTo(ox + w * 0.3f, oy + h * 0.45f)
                    lineTo(ox + w * 0.7f, oy + h * 0.45f)
                }
            }
            'u' -> { //
                addStroke {
                    moveTo(ox + w * 0.25f, oy + h * 0.45f)
                    lineTo(ox + w * 0.25f, oy + h * 0.65f)
                    quadTo(ox + w * 0.5f, oy + h * 0.8f, ox + w * 0.75f, oy + h * 0.65f)
                    lineTo(ox + w * 0.75f, oy + h * 0.45f)
                }
            }
            'v' -> {
                addStroke { moveTo(ox + w * 0.2f, oy + h * 0.45f); lineTo(ox + w * 0.5f, oy + h * 0.75f) }
                addStroke { moveTo(ox + w * 0.5f, oy + h * 0.75f); lineTo(ox + w * 0.8f, oy + h * 0.45f) }
            }
            'w' -> {
                addStroke { moveTo(ox + w * 0.1f, oy + h * 0.45f); lineTo(ox + w * 0.3f, oy + h * 0.75f) }
                addStroke { moveTo(ox + w * 0.3f, oy + h * 0.75f); lineTo(ox + w * 0.5f, oy + h * 0.45f) }
                addStroke { moveTo(ox + w * 0.5f, oy + h * 0.45f); lineTo(ox + w * 0.7f, oy + h * 0.75f) }
                addStroke { moveTo(ox + w * 0.7f, oy + h * 0.75f); lineTo(ox + w * 0.9f, oy + h * 0.45f) }
            }
            'x' -> {
                addStroke { moveTo(ox + w * 0.25f, oy + h * 0.45f); lineTo(ox + w * 0.75f, oy + h * 0.75f) }
                addStroke { moveTo(ox + w * 0.75f, oy + h * 0.45f); lineTo(ox + w * 0.25f, oy + h * 0.75f) }
            }
            'y' -> {
                addStroke {
                    moveTo(ox + w * 0.2f, oy + h * 0.45f)
                    lineTo(ox + w * 0.5f, oy + h * 0.75f)
                }
                addStroke {
                    moveTo(ox + w * 0.8f, oy + h * 0.45f)
                    lineTo(ox + w * 0.5f, oy + h * 0.75f)
                    lineTo(ox + w * 0.4f, oy + h * 0.95f)
                }
            }
            'z' -> {
                addStroke { moveTo(ox + w * 0.2f, oy + h * 0.48f); lineTo(ox + w * 0.8f, oy + h * 0.48f) }
                addStroke { moveTo(ox + w * 0.8f, oy + h * 0.48f); lineTo(ox + w * 0.2f, oy + h * 0.72f) }
                addStroke { moveTo(ox + w * 0.2f, oy + h * 0.72f); lineTo(ox + w * 0.8f, oy + h * 0.72f) }
            }

            else -> {
                addStroke { addRect(ox + w * 0.1f, oy + h * 0.2f, ox + w * 0.9f, oy + h * 0.8f, Path.Direction.CW) }
                Log.w("DrawingWordView", "Strokes not defined for letter: $letter. Using fallback.")
            }
        }
    }

    private fun extractPointsForDefinedStroke(definedStroke: DefinedStroke) {
        definedStroke.points.clear()
        val pm = PathMeasure(definedStroke.path, false)
        if (pm.length == 0f) return
        val point = FloatArray(2)
        val numPoints = 30
        val step = pm.length / numPoints.toFloat()
        var distance = 0f
        while (distance <= pm.length) {
            pm.getPosTan(distance, point, null)
            definedStroke.points.add(PointF(point[0], point[1]))
            distance += step
            if (numPoints > 0 && definedStroke.points.size > numPoints + 2) break
        }
        if (definedStroke.points.isEmpty() && pm.length > 0) {
            pm.getPosTan(0f, point, null); definedStroke.points.add(PointF(point[0], point[1]))
            pm.getPosTan(pm.length, point, null); definedStroke.points.add(PointF(point[0], point[1]))
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isInitialized || currentLetterIndex >= letterBoxes.size) {
            return false
        }

        val activeLetterBox = letterBoxes[currentLetterIndex]
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (activeLetterBox.displayRect.contains(x, y)) {
                    activeDrawingPath.reset()
                    activeDrawingPath.moveTo(x, y)
                    activeLetterBox.currentStrokeAttemptPoints.clear()
                    activeLetterBox.currentStrokeAttemptPoints.add(PointF(x, y))
                    return true
                }
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                if (!activeDrawingPath.isEmpty) {
                    activeDrawingPath.lineTo(x, y)
                    activeLetterBox.currentStrokeAttemptPoints.add(PointF(x, y))
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!activeDrawingPath.isEmpty && activeLetterBox.currentStrokeAttemptPoints.size > 1) {
                    checkActiveLetterStrokeCompletion()
                    invalidate()
                }
                activeDrawingPath.reset()
                activeLetterBox.currentStrokeAttemptPoints.clear()
                return true
            }
        }
        return false
    }

    private fun checkActiveLetterStrokeCompletion() {
        if (currentLetterIndex >= letterBoxes.size) return
        val activeLetterBox = letterBoxes[currentLetterIndex]
        if (activeLetterBox.isLetterFullyCompleted) return

        val userStrokePoints = activeLetterBox.currentStrokeAttemptPoints

        var bestMatchStrokeIndex = -1
        var maxProgress = 0f

        activeLetterBox.definedStrokes.forEachIndexed { index, definedStroke ->
            if (!definedStroke.isCompletedByPlayer) {
                val progress = calculateUserStrokeProgressOnDefinedStroke(userStrokePoints, definedStroke.points)
                if (progress > maxProgress) {
                    maxProgress = progress
                    bestMatchStrokeIndex = index
                }
            }
        }

        if (bestMatchStrokeIndex != -1 && maxProgress >= STROKE_COMPLETION_THRESHOLD) {
            activeLetterBox.definedStrokes[bestMatchStrokeIndex].isCompletedByPlayer = true
            activeLetterBox.completedStrokeIndicesThisLetter.add(bestMatchStrokeIndex)
            Log.d("DrawingWordView", "Letter '${activeLetterBox.letterChar}', Stroke $bestMatchStrokeIndex COMPLETED with progress $maxProgress")

            if (areAllStrokesForLetterCompleted(activeLetterBox)) {
                activeLetterBox.isLetterFullyCompleted = true
                onLetterCompleted?.invoke(activeLetterBox.letterChar, currentLetterIndex)
                Log.i("DrawingWordView", "Letter '${activeLetterBox.letterChar}' FULLY COMPLETED!")

                currentLetterIndex++
                if (currentLetterIndex >= letterBoxes.size) {
                    onCorrectTracing?.invoke()
                    Log.i("DrawingWordView", "Word '$currentWord' FULLY TRACED!")
                }
            }
        } else {
            Log.d("DrawingWordView", "No stroke completed. Best match (stroke $bestMatchStrokeIndex) progress: $maxProgress")
        }
    }

    private fun calculateUserStrokeProgressOnDefinedStroke(userPoints: List<PointF>, templateStrokePoints: List<PointF>): Float {
        if (templateStrokePoints.isEmpty() || userPoints.isEmpty()) return 0f
        var coveredTemplatePoints = 0
        val tolerance = getStrokeToleranceForLetter(letterBoxes[currentLetterIndex])
        for (templatePoint in templateStrokePoints) {
            var foundMatch = false
            for (userPoint in userPoints) {
                if (euclideanDistance(templatePoint, userPoint) <= tolerance) {
                    foundMatch = true
                    break
                }
            }
            if (foundMatch) coveredTemplatePoints++
        }
        return if (templateStrokePoints.isNotEmpty()) coveredTemplatePoints.toFloat() / templateStrokePoints.size else 0f
    }

    private fun getStrokeToleranceForLetter(letterBox: LetterBox): Float {
        val diagonal = sqrt(letterBox.displayRect.width().pow(2) + letterBox.displayRect.height().pow(2))
        return diagonal * STROKE_TOLERANCE_FACTOR
    }

    private fun areAllStrokesForLetterCompleted(letterBox: LetterBox): Boolean {
        if (letterBox.definedStrokes.isEmpty()) return true
        return letterBox.definedStrokes.all { it.isCompletedByPlayer }
    }

    private fun euclideanDistance(p1: PointF, p2: PointF): Float {
        return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInitialized || letterBoxes.isEmpty()) return

        letterBoxes.forEachIndexed { letterIndex, letterBox ->
            val bitmapToDraw = if (letterBox.isLetterFullyCompleted) letterBox.filledBitmap else letterBox.outlineBitmap
            bitmapToDraw?.let { canvas.drawBitmap(it, null, letterBox.displayRect, null) }

            if (letterIndex == currentLetterIndex && !letterBox.isLetterFullyCompleted) {
                letterBox.definedStrokes.forEachIndexed { strokeIndex, definedStroke ->
                    if (definedStroke.isCompletedByPlayer) {
                        canvas.drawPath(definedStroke.path, completedStrokePaint)
                    } else {
                        canvas.drawPath(definedStroke.path, templatePaint)
                    }
                }
            }
            if (letterIndex == currentLetterIndex) {
                if (!activeDrawingPath.isEmpty) {
                    canvas.drawPath(activeDrawingPath, userPaint)
                }
            }
        }
    }
}