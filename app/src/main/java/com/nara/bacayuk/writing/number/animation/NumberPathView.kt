package com.nara.bacayuk.writing.number.animation

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class NumberPathView @JvmOverloads constructor(
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
    private var currentNumber = "A"

    private val pathAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 3000
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { animator ->
            currentLength = animator.animatedValue as Float * totalLength
            invalidate()
        }
    }

    fun setNumber(letter: String) {
        currentNumber = letter
        createNumberPaths()
        startAnimation()
    }

    private fun createNumberPaths() {
        paths.clear()
        when (currentNumber) {
            "0" -> number0()
            "1" -> number1()
            "2" -> number2()
            "3" -> number3()
            "4" -> number4()
            "5" -> number5()
            "6" -> number6()
            "7" -> number7()
            "8" -> number8()
            "9" -> number9()
        }
        calculateTotalLength()
    }

    private fun number0() {
        val path = Path().apply {
            addOval(
                RectF(
                    width * 0.3f, height * 0.2f,
                    width * 0.7f, height * 0.8f
                ),
                Path.Direction.CW
            )
        }
        paths.add(path)
    }

    private fun number1() {

        val path = Path().apply {
            moveTo(width * 0.4f, height * 0.25f)
            lineTo(width * 0.5f, height * 0.2f)
        }
        paths.add(path)

        val path2 = Path().apply {
            moveTo(width * 0.5f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.8f)
        }
        paths.add(path2)
    }

    private fun number2() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.35f)
            quadTo(
                width * 0.6f, height * 0.15f,
                width * 0.7f, height * 0.4f
            )
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.7f, height * 0.4f)
            lineTo(width * 0.3f, height * 0.8f)
            lineTo(width * 0.7f, height * 0.8f)
        }
        paths.add(path2)
    }

    private fun number3() {
        val path1 = Path().apply {
            moveTo(width * 0.2f, height * 0.2f)
            lineTo(width * 0.8f, height * 0.2f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.8f, height * 0.2f)
            lineTo(width * 0.2f, height * 0.5f)
            lineTo(width * 0.8f, height * 0.5f)
            lineTo(width * 0.2f, height * 0.8f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.2f, height * 0.8f)
            lineTo(width * 0.8f, height * 0.8f)
        }
        paths.add(path3)
    }

    private fun number4() {
        val path1 = Path().apply {
            moveTo(width * 0.6f, height * 0.2f)
            lineTo(width * 0.6f, height * 0.8f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.6f, height * 0.2f)
            lineTo(width * 0.35f, height * 0.5f)
        }
        paths.add(path2)

        val path3 = Path().apply {
            moveTo(width * 0.35f, height * 0.5f)
            lineTo(width * 0.7f, height * 0.5f)
        }
        paths.add(path3)
    }

    private fun number5() {
        val path1 = Path().apply {
            moveTo(width * 0.7f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.3f, height * 0.5f)
        }
        paths.add(path1)

        val path2 = Path().apply{
            moveTo(width * 0.3f, height * 0.5f)
            lineTo(width * 0.65f, height * 0.5f)
            arcTo(width * 0.6f,
                height * 0.5f,
                width * 0.7f,
                height * 0.8f,
                270f,
                180f,
                false
            )
            lineTo(width * 0.3f, height * 0.8f)
        }
        paths.add(path2)
    }

    private fun number6() {
        val path1 = Path().apply {
            moveTo(width * 0.58f, height * 0.15f)
            lineTo(width * 0.32f, height * 0.6f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            addOval(
                RectF(
                    width * 0.3f, height * 0.5f,
                    width * 0.7f, height * 0.9f
                ),
                Path.Direction.CW
            )
        }
        paths.add(path2)
    }

    private fun number7() {
        val path1 = Path().apply {
            moveTo(width * 0.3f, height * 0.2f)
            lineTo(width * 0.7f, height * 0.2f)
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.7f, height * 0.2f)
            lineTo(width * 0.35f, height * 0.8f)
        }
        paths.add(path2)
    }

    private fun number8() {
        val path1 = Path().apply {
            addOval(
                RectF(
                    width * 0.3f, height * 0.1f,
                    width * 0.7f, height * 0.5f
                ),
                Path.Direction.CW
            )
        }
        paths.add(path1)

        val path2 = Path().apply {
            addOval(
                RectF(
                    width * 0.3f, height * 0.5f,
                    width * 0.7f, height * 0.9f
                ),
                Path.Direction.CCW
            )
        }
        paths.add(path2)
    }

    private fun number9() {
        val path1 = Path().apply {
            addOval(
                RectF(
                    width * 0.3f, height * 0.1f,
                    width * 0.7f, height * 0.5f
                ),
                Path.Direction.CW
            )
        }
        paths.add(path1)

        val path2 = Path().apply {
            moveTo(width * 0.7f, height * 0.35f)
            lineTo(width * 0.45f, height * 0.8f)
        }
        paths.add(path2)
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
        createNumberPaths()
    }
}