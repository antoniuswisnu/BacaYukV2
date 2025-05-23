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
import androidx.core.graphics.withClip
import kotlin.math.min

class DrawingWordView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- Paint Objects ---
    private val userPaint = Paint().apply { // Renamed from 'paint' for clarity
        color = Color.BLUE // User's drawing color
        style = Paint.Style.STROKE
        strokeWidth = 30f // Initial, will be dynamic
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private val pencilPaint = Paint().apply { // Separate paint for pencil mode if needed
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 30f // Initial, will be dynamic
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private val templatePaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 8f // Initial, will be dynamic
        pathEffect = DashPathEffect(floatArrayOf(15f, 10f), 0f)
        isAntiAlias = true
    }

    private val completedStrokePaint = Paint().apply {
        color = Color.GREEN // Color for correctly traced strokes
        style = Paint.Style.STROKE
        strokeWidth = 10f // Initial, will be dynamic
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        alpha = 150 // Semi-transparent
        isAntiAlias = true
    }

    // --- Drawing State ---
    private var currentWord = ""
    private var currentLetterIndex = 0
    private var letterBoxes = mutableListOf<LetterBox>() // Holds info for each letter in the word
    private var isInitialized = false
    private var activeDrawingPath = Path() // The path user is currently drawing

    // --- Callbacks ---
    private var onCorrectTracing: (() -> Unit)? = null
    private var onLetterCompleted: ((Char, Int) -> Unit)? = null // Callback for when a single letter is done

    // --- Configuration ---
    private val TARGET_LETTER_ASPECT_RATIO = 0.75f // Width/Height for a single letter drawable
    private val STROKE_COMPLETION_THRESHOLD = 0.7f // Percentage of stroke points user needs to cover
    private val STROKE_TOLERANCE_FACTOR = 0.08f // Tolerance for matching user points to template stroke (percentage of letter diagonal)


    // --- Data Class for Individual Letter Strokes (adapted from DrawingLetterCapitalView) ---
    private data class DefinedStroke(
        val path: Path, // The template path for this stroke, relative to 0,0 of letter box
        val points: MutableList<PointF> = mutableListOf(), // Sampled points along the path
        var isCompletedByPlayer: Boolean = false
    )

    // --- Data Class for Each Letter in the Word ---
    private data class LetterBox(
        val letterChar: Char,
        var displayRect: RectF, // The actual drawing rectangle for the letter on canvas
        var outlineBitmap: Bitmap? = null,
        var filledBitmap: Bitmap? = null,
        val definedStrokes: MutableList<DefinedStroke> = mutableListOf(), // Strokes for this specific letter
        var userPathsForThisLetter: MutableList<Path> = mutableListOf(), // User's drawn paths for current attempt on this letter
        var currentStrokeAttemptPoints: MutableList<PointF> = mutableListOf(), // Points for the very current user stroke
        var completedStrokeIndicesThisLetter: MutableSet<Int> = mutableSetOf(), // Indices of completed strokes for this letter
        var isLetterFullyCompleted: Boolean = false // If all required strokes for this letter are done
    )

    init {
        // Set current drawing paint
        userPaint.set(pencilPaint) // Default to pencil
    }
    // --- Public API ---
    fun setDrawingMode(isPencilMode: Boolean) {
        // If you had an eraser mode, you'd switch paint here
        // For now, pencilPaint is always used by userPaint
        // userPaint.set(if (isPencilMode) pencilPaint else eraserPaint)
        invalidate()
    }

    fun clearCanvas() { // Clears the current attempt on the active letter
        if (currentLetterIndex < letterBoxes.size) {
            val activeLetterBox = letterBoxes[currentLetterIndex]
            activeLetterBox.userPathsForThisLetter.clear()
            activeLetterBox.currentStrokeAttemptPoints.clear()
            // Do NOT reset completedStrokeIndicesThisLetter or isLetterFullyCompleted here,
            // as clearCanvas is usually for retrying the current stroke/letter, not full reset.
        }
        activeDrawingPath.reset()
        invalidate()
    }

    fun resetCurrentLetterProgress() { // Resets all progress for the currently active letter
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
        currentWord = word.uppercase()
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

    // --- Lifecycle and Sizing ---
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

        val displayLetterHeight = viewHeight
        val displayLetterWidth: Float
        val startX: Float

        if (totalIdealWordWidth <= viewWidth) {
            displayLetterWidth = idealLetterWidth
            startX = (viewWidth - totalIdealWordWidth) / 2f
        } else {
            displayLetterWidth = viewWidth / currentWord.length
            startX = 0f
        }

        // Dynamic stroke widths
        val baseStrokeSize = displayLetterWidth * 0.1f // Example: 10% of letter width
        userPaint.strokeWidth = baseStrokeSize
        pencilPaint.strokeWidth = baseStrokeSize // if used separately
        templatePaint.strokeWidth = baseStrokeSize * 0.25f // Thinner for template
        completedStrokePaint.strokeWidth = baseStrokeSize * 0.3f


        currentWord.forEachIndexed { index, char ->
            val letterLeft = startX + (index * displayLetterWidth)
            val rect = RectF(letterLeft, 0f, letterLeft + displayLetterWidth, displayLetterHeight)
            val bitmapW = displayLetterWidth.toInt().coerceAtLeast(1)
            val bitmapH = displayLetterHeight.toInt().coerceAtLeast(1)

            val newLetterBox = LetterBox(
                letterChar = char,
                displayRect = rect,
                outlineBitmap = getBitmapForLetter(char, false, bitmapW, bitmapH),
                filledBitmap = getBitmapForLetter(char, true, bitmapW, bitmapH)
            )
            defineStrokesForLetter(char, newLetterBox.definedStrokes, rect) // Define strokes for this char
            letterBoxes.add(newLetterBox)
        }
    }

    // --- Bitmap Loading (similar to before) ---
    @SuppressLint("DiscouragedApi")
    private fun getBitmapForLetter(letter: Char, isFilled: Boolean, targetWidth: Int, targetHeight: Int): Bitmap? {
        if (targetWidth <= 0 || targetHeight <= 0) return null
        val resourceName = "huruf_${letter.lowercaseChar()}_${if (isFilled) "fill" else "outline"}"
        val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resourceId == 0) {
            Log.w("DrawingWordView", "Drawable resource not found: $resourceName")
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


    // --- Stroke Definition Logic (Adapted from DrawingLetterCapitalView) ---
    private fun defineStrokesForLetter(letter: Char, strokesList: MutableList<DefinedStroke>, letterRect: RectF) {
        strokesList.clear()
        val w = letterRect.width()
        val h = letterRect.height()
        val ox = letterRect.left // Offset X for this letter's rect
        val oy = letterRect.top  // Offset Y for this letter's rect

        if (w <= 0 || h <= 0) return // Cannot define strokes for an empty rect

        // Helper to create and add a stroke
        fun addStroke(isRequired: Boolean = true, pathBuilder: Path.() -> Unit) {
            val path = Path()
            path.pathBuilder()
            val definedStroke = DefinedStroke(path = path)
            extractPointsForDefinedStroke(definedStroke) // Sample points
            strokesList.add(definedStroke)
        }

        when (letter) {
            'A' -> {
                addStroke { moveTo(ox + w * 0.1f, oy + h * 0.8f); lineTo(ox + w * 0.5f, oy + h * 0.25f) }
                addStroke { moveTo(ox + w * 0.5f, oy + h * 0.25f); lineTo(ox + w * 0.9f, oy + h * 0.8f) }
                addStroke { moveTo(ox + w * 0.24f, oy + h * 0.65f); lineTo(ox + w * 0.75f, oy + h * 0.65f) }
            }
            'B' -> {
                addStroke { moveTo(ox + w * 0.19f, oy + h * 0.26f); lineTo(ox + w * 0.19f, oy + h * 0.8f) }
                addStroke {
                    moveTo(ox + w * 0.19f, oy + h * 0.26f); lineTo(ox + w * 0.6f, oy + h * 0.26f)
                    arcTo(ox + w * 0.5f, oy + h * 0.26f, ox + w * 0.8f, oy + h * 0.5f, 270f, 180f, false)
                    lineTo(ox + w * 0.19f, oy + h * 0.5f)
                }
                addStroke {
                    moveTo(ox + w * 0.19f, oy + h * 0.5f); lineTo(ox + w * 0.6f, oy + h * 0.5f)
                    arcTo(ox + w * 0.5f, oy + h * 0.5f, ox + w * 0.8f, oy + h * 0.8f, 270f, 180f, false)
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
            // Add all other letter stroke definitions here, similar to 'A', 'B', 'C'
            // Make sure to use 'ox' and 'oy' for offsets and 'w' and 'h' for dimensions.
            // Example for 'I':
            'I' -> {
                addStroke { moveTo(ox + w * 0.5f, oy + h * 0.26f); lineTo(ox + w * 0.5f, oy + h * 0.8f) }
            }
            // ... (ensure all letters A-Z from your original createTemplatePath are converted)
            else -> { // Fallback for undefined letters
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
        val numPoints = 30 // Number of sample points per stroke
        val step = pm.length / numPoints.toFloat()
        var distance = 0f
        while (distance <= pm.length) {
            pm.getPosTan(distance, point, null)
            definedStroke.points.add(PointF(point[0], point[1]))
            distance += step
            if (numPoints > 0 && definedStroke.points.size > numPoints + 2) break // Safety break
        }
        if (definedStroke.points.isEmpty() && pm.length > 0) { // Ensure at least start and end if step is too large
            pm.getPosTan(0f, point, null); definedStroke.points.add(PointF(point[0], point[1]))
            pm.getPosTan(pm.length, point, null); definedStroke.points.add(PointF(point[0], point[1]))
        }
    }


    // --- Touch Event Handling (Adapted for Stroke Logic) ---
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isInitialized || currentLetterIndex >= letterBoxes.size) {
            return false // Not ready or word completed
        }

        val activeLetterBox = letterBoxes[currentLetterIndex]
        // Transform event coordinates to be relative to the active letter box if needed,
        // but since strokes are defined with absolute canvas coordinates (ox, oy), direct event.x/y is fine.
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Check if touch is within the general area of the active letter. More precise checks can be added.
                if (activeLetterBox.displayRect.contains(x, y)) {
                    activeDrawingPath.reset()
                    activeDrawingPath.moveTo(x, y)
                    activeLetterBox.currentStrokeAttemptPoints.clear()
                    activeLetterBox.currentStrokeAttemptPoints.add(PointF(x, y))
                    return true
                }
                return false // Touch down outside active letter
            }
            MotionEvent.ACTION_MOVE -> {
                if (!activeDrawingPath.isEmpty) { // Only if ACTION_DOWN was processed
                    activeDrawingPath.lineTo(x, y)
                    activeLetterBox.currentStrokeAttemptPoints.add(PointF(x, y))
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!activeDrawingPath.isEmpty && activeLetterBox.currentStrokeAttemptPoints.size > 1) {
                    activeLetterBox.userPathsForThisLetter.add(Path(activeDrawingPath)) // Store a copy
                    checkActiveLetterStrokeCompletion()
                    invalidate()
                }
                activeDrawingPath.reset() // Reset for next touch
                activeLetterBox.currentStrokeAttemptPoints.clear() // Clear points for next stroke
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

        // Find which defined stroke the user was most likely trying to draw
        activeLetterBox.definedStrokes.forEachIndexed { index, definedStroke ->
            if (!definedStroke.isCompletedByPlayer) { // Only check uncompleted template strokes
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

            // Check if all strokes for this letter are now complete
            if (areAllStrokesForLetterCompleted(activeLetterBox)) {
                activeLetterBox.isLetterFullyCompleted = true
                onLetterCompleted?.invoke(activeLetterBox.letterChar, currentLetterIndex)
                Log.i("DrawingWordView", "Letter '${activeLetterBox.letterChar}' FULLY COMPLETED!")

                currentLetterIndex++ // Move to next letter
                if (currentLetterIndex >= letterBoxes.size) {
                    onCorrectTracing?.invoke() // Entire word is done
                    Log.i("DrawingWordView", "Word '$currentWord' FULLY TRACED!")
                }
            }
        } else {
            Log.d("DrawingWordView", "No stroke completed. Best match (stroke $bestMatchStrokeIndex) progress: $maxProgress")
        }
        // Clear the user's drawn path for this specific attempt, whether successful or not,
        // so they draw the next stroke cleanly.
        // activeLetterBox.userPathsForThisLetter.clear() // Or just don't add if not good enough?
        // For now, let's keep drawn paths for visual feedback until letter is reset/cleared.
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
            if (foundMatch) {
                coveredTemplatePoints++
            }
        }
        return if (templateStrokePoints.isNotEmpty()) coveredTemplatePoints.toFloat() / templateStrokePoints.size else 0f
    }

    private fun getStrokeToleranceForLetter(letterBox: LetterBox): Float {
        val diagonal = sqrt(letterBox.displayRect.width().pow(2) + letterBox.displayRect.height().pow(2))
        return diagonal * STROKE_TOLERANCE_FACTOR // e.g., 8% of the letter's diagonal
    }

    private fun areAllStrokesForLetterCompleted(letterBox: LetterBox): Boolean {
        if (letterBox.definedStrokes.isEmpty()) return true // No strokes to complete
        return letterBox.definedStrokes.all { it.isCompletedByPlayer }
    }

    private fun euclideanDistance(p1: PointF, p2: PointF): Float {
        return sqrt((p1.x - p2.x).pow(2) + (p1.y - p2.y).pow(2))
    }

    // --- Drawing Logic (Adapted for Stroke-based feedback) ---
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInitialized || letterBoxes.isEmpty()) return

        letterBoxes.forEachIndexed { letterIndex, letterBox ->
            // 1. Draw Bitmaps (Outline/Filled)
            val bitmapToDraw = if (letterBox.isLetterFullyCompleted) letterBox.filledBitmap else letterBox.outlineBitmap
            bitmapToDraw?.let { canvas.drawBitmap(it, null, letterBox.displayRect, null) }

            // 2. Draw Template Strokes and Completed Strokes for the current letter
            if (letterIndex == currentLetterIndex && !letterBox.isLetterFullyCompleted) {
                letterBox.definedStrokes.forEachIndexed { strokeIndex, definedStroke ->
                    if (definedStroke.isCompletedByPlayer) {
                        canvas.drawPath(definedStroke.path, completedStrokePaint)
                    } else {
                        canvas.drawPath(definedStroke.path, templatePaint)
                    }
                }
            }
            // Or, if you want to show completed strokes even after letter is done (but before word is done)
            // else if (letterBox.isLetterFullyCompleted && letterIndex < currentWord.length) {
            //    letterBox.definedStrokes.forEach { canvas.drawPath(it.path, completedStrokePaint) }
            // }


            // 3. Draw User's Paths for the current active letter
            if (letterIndex == currentLetterIndex) {
                // Draw stored paths for the current letter (multiple strokes attempt)
                // letterBox.userPathsForThisLetter.forEach { path ->
                //    canvas.drawPath(path, userPaint)
                // }
                // Draw the very current, actively being drawn path
                if (!activeDrawingPath.isEmpty) {
                    canvas.drawPath(activeDrawingPath, userPaint)
                }
            }
        }
    }
}