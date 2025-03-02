package com.nara.bacayuk.writing.letter.animation.capital

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class LetterPathCapitalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val pathPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private val paths = mutableListOf<Path>()
    private var pathMeasure = PathMeasure()
    private var currentLength = 0f
    private var totalLength = 0f
    private var currentPath = 0

    private val drawPath = Path()
    private var currentLetter = "A"

    private val pathAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 3000
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { animator ->
            currentLength = animator.animatedValue as Float * totalLength
            invalidate()
        }
    }

    fun setLetter(letter: String) {
        currentLetter = letter
        createLetterPaths()
        startAnimation()
    }

    private fun createLetterPaths() {
        paths.clear()
        when (currentLetter) {
            "A" -> createLetterA()
            "B" -> createLetterB()
            "C" -> createLetterC()
            "D" -> createLetterD()
            "E" -> createLetterE()
            "F" -> createLetterF()
            "G" -> createLetterG()
            "H" -> createLetterH()
            "I" -> createLetterI()
            "J" -> createLetterJ()
            "K" -> createLetterK()
            "L" -> createLetterL()
            "M" -> createLetterM()
            "N" -> createLetterN()
            "O" -> createLetterO()
            "P" -> createLetterP()
            "Q" -> createLetterQ()
            "R" -> createLetterR()
            "S" -> createLetterS()
            "T" -> createLetterT()
            "U" -> createLetterU()
            "V" -> createLetterV()
            "W" -> createLetterW()
            "X" -> createLetterX()
            "Y" -> createLetterY()
            "Z" -> createLetterZ()
        }
        calculateTotalLength()
    }

    private fun createLetterA() {
        val path1 = Path().apply {
            moveTo(width * 0.2f, height * 0.8f)
            lineTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.8f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.35f, height * 0.5f)
            lineTo(width * 0.65f, height * 0.5f)
        }
        paths.add(path2)
    }

    private fun createLetterB() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            cubicTo(
                width * 0.8f, height * 0.2f,
                width * 0.8f, height * 0.45f,
                width * 0.3f, height * 0.5f
            )
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.3f, height * 0.5f)
            cubicTo(
                width * 0.8f, height * 0.5f,
                width * 0.8f, height * 0.8f,
                width * 0.3f, height * 0.8f
            )
        }
        paths.add(path3)
    }

    private fun createLetterC() {
        val path = Path().apply {
            moveTo(width * 0.8f, height * 0.3f)
            cubicTo(
                width * 0.8f, height * 0.2f,
                width * 0.6f, height * 0.2f,
                width * 0.4f, height * 0.2f
            )
            cubicTo(
                width * 0.2f, height * 0.2f,
                width * 0.2f, height * 0.5f,
                width * 0.2f, height * 0.8f
            )
            cubicTo(
                width * 0.2f, height * 0.8f,
                width * 0.4f, height * 0.8f,
                width * 0.8f, height * 0.7f
            )
        }
        paths.add(path)
    }

    private fun createLetterD() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            cubicTo(
                width * 0.6f, height * 0.2f,
                width * 0.65f, height * 0.4f,
                width * 0.65f, height * 0.5f
            )
            cubicTo(
                width * 0.65f, height * 0.6f,
                width * 0.6f, height * 0.8f,
                width * 0.3f, height * 0.8f
            )
        }
        paths.add(path2)
    }

    private fun createLetterE() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.6f, height * 0.2f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.3f, height * 0.5f)
            lineTo(width * 0.6f, height * 0.5f)
        }
        paths.add(path3)

        val path4 = Path().apply {
            moveTo(width * 0.3f, height * 0.8f)
            lineTo(width * 0.6f, height * 0.8f)
        }
        paths.add(path4)
    }

    private fun createLetterF() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.9f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.2f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.3f, height * 0.5f)
            lineTo(width * 0.5f, height * 0.5f)
        }
        paths.add(path3)
    }

    private fun createLetterG() {
        val path1 = Path().apply {
            moveTo(width * 0.8f, height * 0.3f)
            cubicTo(
                width * 0.8f, height * 0.2f,
                width * 0.6f, height * 0.2f,
                width * 0.4f, height * 0.2f
            )
            cubicTo(
                width * 0.2f, height * 0.2f,
                width * 0.2f, height * 0.5f,
                width * 0.2f, height * 0.8f
            )
            cubicTo(
                width * 0.2f, height * 0.8f,
                width * 0.4f, height * 0.8f,
                width * 0.8f, height * 0.7f
            )
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.8f, height * 0.7f)   // Bottom right
            lineTo(width * 0.8f, height * 0.5f)   // Vertical line
            lineTo(width * 0.5f, height * 0.5f)   // Horizontal line
        }
        paths.add(path2)
    }

    private fun createLetterH() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.7f, height * 0.2f)
            lineTo(width * 0.7f, height * 0.8f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.3f, height * 0.5f)
            lineTo(width * 0.7f, height * 0.5f)
        }
        paths.add(path3)
    }

    private fun createLetterI() {
        val path = Path().apply {
            moveTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.8f)
        }
        paths.add(path)


        val path2 = Path().apply {
//            addCircle(width * 0.5f, height * 0.1f, 10f, Path.Direction.CW)
            moveTo(width * 0.4f, height * 0.2f)
            lineTo(width * 0.6f, height * 0.2f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.4f, height * 0.8f)
            lineTo(width * 0.6f, height * 0.8f)
        }
        paths.add(path3)
    }

    private fun createLetterJ() {
        val path = Path().apply {
            moveTo(width * 0.6f, height * 0.2f)
            lineTo(width * 0.6f, height * 0.6f)
            cubicTo(
                width * 0.6f, height * 0.8f,
                width * 0.5f, height * 0.8f,
                width * 0.3f, height * 0.7f
            )
        }
        paths.add(path)
    }

    private fun createLetterK() {
        val path1 = Path().apply {
            moveTo(width * 0.2f, height * 0.2f)
            lineTo(width * 0.2f, height * 1.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.2f, height * 0.6f)
            lineTo(width * 0.6f, height * 0.2f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.3f, height * 0.5f)
            lineTo(width * 1.0f, height * 1.8f)
        }
        paths.add(path3)
    }

    private fun createLetterL() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.9f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.3f, height * 0.9f)
            lineTo(width * 0.6f, height * 0.9f)
        }
        paths.add(path2)
    }

    private fun createLetterM() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.8f)
            lineTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.5f)
            lineTo(width * 0.7f, height * 0.2f)
            lineTo(width * 0.7f, height * 0.8f)
        }
        paths.add(path1)
    }

    private fun createLetterN() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.8f)
            lineTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.7f, height * 0.8f)
            lineTo(width * 0.7f, height * 0.2f)
        }
        paths.add(path1)
    }

    private fun createLetterO() {
        val path = Path().apply {
            addOval(
                RectF(
                    width * 0.3f, height * 0.2f,
                    width * 0.7f, height * 0.9f
                ),
                Path.Direction.CW
            )
        }
        paths.add(path)
    }

    private fun createLetterP() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.9f)
            lineTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.2f)
            arcTo(width * 0.3f,
                height * 0.2f,
                width * 0.6f,
                height * 0.5f,
                270f,
                180f,
                false
            )
            lineTo(width * 0.3f, height * 0.5f)
        }
        paths.add(path1)
    }

    private fun createLetterQ() {
        val path1 = Path().apply {
           addOval(
               RectF(
                   width * 0.3f, height * 0.2f,
                   width * 0.7f, height * 0.9f
               ),
               Path.Direction.CW
           )
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.5f, height * 0.7f)
            lineTo(width * 0.65f, height * 1.1f)
        }
        paths.add(path2)
    }

    private fun createLetterR() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.8f)
            lineTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.2f)
            arcTo(width * 0.4f,
                height * 0.2f,
                width * 0.6f,
                height * 0.5f,
                270f,
                180f,
                false
            )
            lineTo(width * 0.3f, height * 0.5f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.5f, height * 0.5f)
            lineTo(width * 0.7f, height * 0.8f)
        }
        paths.add(path2)
    }

    private fun createLetterS() {
        val path1 = Path().apply {
            moveTo(width * 0.8f, height * 0.2f)   // Top right
            cubicTo(
                width * 0.8f, height * 0.1f,      // Control point 1
                width * 0.6f, height * 0.1f,      // Control point 2
                width * 0.4f, height * 0.1f       // End point top
            )
            cubicTo(
                width * 0.2f, height * 0.1f,      // Control point 1
                width * 0.2f, height * 0.4f,      // Control point 2
                width * 0.2f, height * 0.5f       // End point middle
            )
            cubicTo(
                width * 0.2f, height * 0.6f,      // Control point 1
                width * 0.4f, height * 0.6f,      // Control point 2
                width * 0.6f, height * 0.6f       // End point middle
            )
            cubicTo(
                width * 0.8f, height * 0.6f,      // Control point 1
                width * 0.8f, height * 0.9f,      // Control point 2
                width * 0.8f, height * 0.8f       // End point bottom
            )

        }
        paths.add(path1)
    }

    private fun createLetterT() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.7f, height * 0.2f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.8f)
        }
        paths.add(path2)
    }

    private fun createLetterU() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.7f)
            cubicTo(
                width * 0.3f, height * 0.8f,
                width * 0.4f, height * 0.8f,
                width * 0.4f, height * 0.8f
            )
            lineTo(width * 0.6f, height * 0.8f)
            cubicTo(
                width * 0.6f, height * 0.8f,
                width * 0.7f, height * 0.8f,
                width * 0.7f, height * 0.7f
            )
            lineTo(width * 0.7f, height * 0.2f)
        }
        paths.add(path1)
    }

    private fun createLetterV() {
        val path = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.8f)
            lineTo(width * 0.7f, height * 0.2f)
        }
        paths.add(path)
    }

    private fun createLetterW() {
        val path = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.35f, height * 0.8f)
            lineTo(width * 0.5f, height * 0.5f)
            lineTo(width * 0.65f, height * 0.8f)
            lineTo(width * 0.7f, height * 0.2f)
        }
        paths.add(path)
    }

    private fun createLetterX() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.7f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.7f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.8f)

        }
        paths.add(path2)
    }

    private fun createLetterY() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.5f)
            lineTo(width * 0.7f, height * 0.2f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.5f, height * 0.5f)
            lineTo(width * 0.5f, height * 0.9f)
        }
        paths.add(path2)
    }

    private fun createLetterZ() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.7f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.8f)
            lineTo(width * 0.7f, height * 0.8f)
        }
        paths.add(path1)
    }

    private fun calculateTotalLength() {
        totalLength = 0f
        paths.forEach { path ->
            pathMeasure.setPath(path, false)
            totalLength += pathMeasure.length
        }
    }

    private fun startAnimation() {
        pathAnimator.cancel()
        currentLength = 0f
        currentPath = 0
        pathAnimator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var lengthSoFar = 0f
        var remainingLength = currentLength

        for (i in paths.indices) {
            pathMeasure.setPath(paths[i], false)
            val pathLength = pathMeasure.length

            if (remainingLength > pathLength) {
                canvas.drawPath(paths[i], pathPaint)
                remainingLength -= pathLength
            } else if (remainingLength > 0) {
                drawPath.reset()
                pathMeasure.getSegment(0f, remainingLength, drawPath, true)
                canvas.drawPath(drawPath, pathPaint)
                remainingLength = 0f
            }

            lengthSoFar += pathLength
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createLetterPaths()
    }
}