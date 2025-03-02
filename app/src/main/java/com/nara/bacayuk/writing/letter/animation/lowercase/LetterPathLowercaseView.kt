package com.nara.bacayuk.writing.letter.animation.lowercase

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class LetterPathLowercaseView @JvmOverloads constructor(
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
    private var currentLetter = "a"

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
            "a" -> createLetterA()
            "b" -> createLetterB()
            "c" -> createLetterC()
            "d" -> createLetterD()
            "e" -> createLetterE()
            "f" -> createLetterF()
            "g" -> createLetterG()
            "h" -> createLetterH()
            "i" -> createLetterI()
            "j" -> createLetterJ()
            "k" -> createLetterK()
            "l" -> createLetterL()
            "m" -> createLetterM()
            "n" -> createLetterN()
            "o" -> createLetterO()
            "p" -> createLetterP()
            "q" -> createLetterQ()
            "r" -> createLetterR()
            "s" -> createLetterS()
            "t" -> createLetterT()
            "u" -> createLetterU()
            "v" -> createLetterV()
            "w" -> createLetterW()
            "x" -> createLetterX()
            "y" -> createLetterY()
            "z" -> createLetterZ()
        }
        calculateTotalLength()
    }

    private fun createLetterA() {
        val path1 = Path().apply {
            moveTo(width * 0.4f, height * 0.5f)
            quadTo(
                width * 0.5f, height * 0.4f,
                width * 0.6f, height * 0.5f
            )
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.6f, height * 0.5f)
            lineTo(width * 0.6f, height * 0.8f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.6f, height * 0.7f)
            arcTo(
                width * 0.4f, height * 0.6f,
                width * 0.6f, height * 0.8f,
                0f,
                -270f,
                false
            )
            lineTo(width * 0.6f, height * 0.8f)
        }
        paths.add(path3)
    }

    private fun createLetterB() {
        val path1 = Path().apply {
            moveTo(width * 0.4f, height * 0.2f)
            lineTo(width * 0.4f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.4f, height * 0.5f)
            quadTo(
                width * 0.7f, height * 0.6f,
                width * 0.4f, height * 0.8f
            )
        }
        paths.add(path2)
    }

    private fun createLetterC() {
        val path1 = Path().apply {
//            moveTo(width * 0.6f, height * 0.4f)
          arcTo(RectF(width * 0.3f, height * 0.3f,
                                width * 0.8f, height * 0.7f),
                                270f, -180f)
        }
        paths.add(path1)
    }

    private fun createLetterD() {
        val path1 = Path().apply {
            moveTo(width * 0.6f, height * 0.2f)
            lineTo(width * 0.6f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.6f, height * 0.6f)
            lineTo(width * 0.4f, height * 0.6f)
            quadTo(
                width * 0.35f, height * 0.6f,
                width * 0.35f, height * 0.8f
            )
            lineTo(width * 0.6f, height * 0.8f)
        }
        paths.add(path2)
    }

    private fun createLetterE() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.4f)
            lineTo(width * 0.7f, height * 0.4f)
        }
        paths.add(path1)

        val path2 = Path().apply {
//            moveTo(width * 0.7f, height * 0.4f)
            arcTo(RectF(width * 0.3f, height * 0.2f,
                width * 0.7f, height * 0.6f), 180f, 180f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.3f, height * 0.4f)
            quadTo(
                width * 0.3f, height * 0.7f,
                width * 0.65f, height * 0.6f
            )
        }
        paths.add(path3)
    }

    private fun createLetterF() {
        val path1 = Path().apply {
            moveTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.4f, height * 0.4f)
            lineTo(width * 0.6f, height * 0.4f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.5f, height * 0.2f)
            quadTo(
                width * 0.5f, height * 0.2f,
                width * 0.6f, height * 0.1f
            )
        }
        paths.add(path3)
    }

    private fun createLetterG() {
        val path1 = Path().apply {
            moveTo(width * 0.6f, height * 0.4f)
            arcTo(RectF(width * 0.4f, height * 0.4f,
                width * 0.6f, height * 0.6f),
                0f, -340f
            )
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.6f, height * 0.4f)
            lineTo(width * 0.6f, height * 0.7f)
            quadTo(
                width * 0.45f, height * 0.8f,
                width * 0.45f, height * 0.7f
            )
        }
        paths.add(path2)
    }

    private fun createLetterH() {
        val path1 = Path().apply {
            moveTo(width * 0.4f, height * 0.2f)
            lineTo(width * 0.4f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.4f, height * 0.5f)
            quadTo(
                width * 0.6f, height * 0.4f,
                width * 0.6f, height * 0.5f
            )
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.6f, height * 0.5f)
            lineTo(width * 0.6f, height * 0.8f)
        }
        paths.add(path3)
    }

    private fun createLetterI() {
        val path1 = Path().apply {
            moveTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            addCircle(width * 0.5f, height * 0.1f, 10f, Path.Direction.CW)
        }
        paths.add(path2)
    }

    private fun createLetterJ() {
        val path1 = Path().apply {
            moveTo(width * 0.6f, height * 0.2f)
            lineTo(width * 0.6f, height * 0.6f)
            cubicTo(
                width * 0.6f, height * 0.8f,
                width * 0.5f, height * 0.8f,
                width * 0.3f, height * 0.7f
            )
        }
        paths.add(path1)

        val path2 = Path().apply {
            addCircle(width * 0.6f, height * 0.1f, 10f, Path.Direction.CW)
        }
        paths.add(path2)
    }

    private fun createLetterK() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.3f, height * 0.6f)
            lineTo(width * 0.5f, height * 0.4f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.4f, height * 0.5f)
            lineTo(width * 0.55f, height * 0.8f)
        }
        paths.add(path3)
    }

    private fun createLetterL() {
        val path1 = Path().apply {
            moveTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.8f)
        }
        paths.add(path1)
    }

    private fun createLetterM() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.7f)
            lineTo(width * 0.3f, height * 0.28f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.3f, height * 0.3f)
            quadTo(
                width * 0.5f, height * 0.2f,
                width * 0.5f, height * 0.3f
            )
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.5f, height * 0.3f)
            lineTo(width * 0.5f, height * 0.7f)
        }
        paths.add(path3)

        val path4 = Path().apply {
            moveTo(width * 0.5f, height * 0.3f)
            quadTo(
                width * 0.7f, height * 0.2f,
                width * 0.7f, height * 0.3f
            )
        }
        paths.add(path4)

        val path5 = Path().apply {
            moveTo(width * 0.7f, height * 0.3f)
            lineTo(width * 0.7f, height * 0.7f)
        }
        paths.add(path5)
    }

    private fun createLetterN() {
        val path1 = Path().apply {
            moveTo(width * 0.4f, height * 0.7f)
            lineTo(width * 0.4f, height * 0.28f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.4f, height * 0.3f)
            quadTo(
                width * 0.6f, height * 0.2f,
                width * 0.6f, height * 0.3f
            )
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.6f, height * 0.3f)
            lineTo(width * 0.6f, height * 0.7f)
        }
        paths.add(path3)
    }

    private fun createLetterO() {
        val path = Path().apply {
            addCircle(width * 0.5f, height * 0.5f, width * 0.2f, Path.Direction.CW)
        }
        paths.add(path)
    }

    private fun createLetterP() {
        val path1 = Path().apply {
            moveTo(width * 0.4f, height * 0.2f)
            lineTo(width * 0.4f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.4f, height * 0.23f)
            lineTo(width * 0.45f, height * 0.23f)
            arcTo(width * 0.3f,
                height * 0.23f,
                width * 0.6f,
                height * 0.5f,
                270f,
                180f,
                false
            )
            lineTo(width * 0.4f, height * 0.5f)
        }
        paths.add(path2)
    }

    private fun createLetterQ() {
        val path1 = Path().apply {
            moveTo(width * 0.6f, height * 0.4f)
            lineTo(width * 0.6f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.6f, height * 0.4f)
            arcTo(
                RectF(
                    width * 0.3f, height * 0.2f,
                    width * 0.7f, height * 0.6f
                ),
                0f, 180f
            )
        }
        paths.add(path2)
    }

    private fun createLetterR() {
        val path1 = Path().apply {
            moveTo(width * 0.4f, height * 0.2f)
            lineTo(width * 0.4f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.4f, height * 0.24f)
            quadTo(
                width * 0.55f, height * 0.18f,
                width * 0.55f, height * 0.2f
            )
        }
        paths.add(path2)
    }

    private fun createLetterS() {
        val path1 = Path().apply {
            moveTo(width * 0.7f, height * 0.4f)
            quadTo(
                width * 0.3f, height * 0.4f,
                width * 0.7f, height * 0.6f
            )
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.7f, height * 0.6f)
            quadTo(
                width * 0.3f, height * 0.6f,
                width * 0.7f, height * 0.8f
            )
        }
        paths.add(path2)
    }

    private fun createLetterT() {
        val path1 = Path().apply {
            moveTo(width * 0.5f, height * 0.1f)
            lineTo(width * 0.5f, height * 0.7f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.5f, height * 0.7f)
           quadTo(
                width * 0.55f, height * 0.7f,
                width * 0.55f, height * 0.68f
            )
        }
        paths.add(path2)


        val path3 = Path().apply {
            moveTo(width * 0.4f, height * 0.2f)
            lineTo(width * 0.6f, height * 0.2f)
        }
        paths.add(path3)
    }

    private fun createLetterU() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.3f)
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
            lineTo(width * 0.7f, height * 0.3f)
        }
        paths.add(path1)    }

    private fun createLetterV() {
        val path = Path().apply {
            moveTo(width * 0.3f, height * 0.4f)
            lineTo(width * 0.5f, height * 0.8f)
            lineTo(width * 0.7f, height * 0.4f)
        }
        paths.add(path)
    }

    private fun createLetterW() {
        val path = Path().apply {
            moveTo(width * 0.3f, height * 0.45f)
            lineTo(width * 0.35f, height * 0.8f)
            lineTo(width * 0.5f, height * 0.6f)
            lineTo(width * 0.65f, height * 0.8f)
            lineTo(width * 0.7f, height * 0.45f)
        }
        paths.add(path)
    }

    private fun createLetterX() {
        val path1 = Path().apply {
            moveTo(width * 0.35f, height * 0.3f)
            lineTo(width * 0.65f, height * 0.7f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.65f, height * 0.3f)
            lineTo(width * 0.35f, height * 0.7f)
        }
        paths.add(path2)
    }

    private fun createLetterY() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.4f)
            lineTo(width * 0.5f, height * 0.6f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.5f, height * 0.6f)
            lineTo(width * 0.7f, height * 0.4f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.5f, height * 0.6f)
            lineTo(width * 0.5f, height * 0.8f)
        }
        paths.add(path3)
    }

    private fun createLetterZ() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.3f)
            lineTo(width * 0.7f, height * 0.3f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.7f, height * 0.3f)
            lineTo(width * 0.3f, height * 0.8f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.3f, height * 0.8f)
            lineTo(width * 0.7f, height * 0.8f)
        }
        paths.add(path3)
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
        startAnimation()
    }
}