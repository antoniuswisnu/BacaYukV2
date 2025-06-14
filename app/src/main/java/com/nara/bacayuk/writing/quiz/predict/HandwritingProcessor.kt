package com.nara.bacayuk.writing.quiz.predict

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.tensorflow.lite.Interpreter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import java.util.*
import androidx.core.graphics.scale

class HandwritingProcessor(private val context: Context, private val tflite: Interpreter) {

    companion object {
        private const val TAG = "HandwritingProcessor"
        private const val INPUT_SIZE = 28
        private const val THRESHOLD = 200
    }

    private val labelMapping = mapOf(
        0 to "0", 1 to "1", 2 to "2", 3 to "3", 4 to "4",
        5 to "5", 6 to "6", 7 to "7", 8 to "8", 9 to "9",
        10 to "A", 11 to "B", 12 to "C", 13 to "D", 14 to "E",
        15 to "F", 16 to "G", 17 to "H", 18 to "I", 19 to "J",
        20 to "K", 21 to "L", 22 to "M", 23 to "N", 24 to "O",
        25 to "P", 26 to "Q", 27 to "R", 28 to "S", 29 to "T",
        30 to "U", 31 to "V", 32 to "W", 33 to "X", 34 to "Y",
        35 to "Z", 36 to "a", 37 to "b", 38 to "d", 39 to "e",
        40 to "f", 41 to "g", 42 to "h", 43 to "n", 44 to "q",
        45 to "r", 46 to "t"
    )

    fun processImage(bitmap: Bitmap): String {
        Log.d(TAG, "===== MEMULAI PENGENALAN TULISAN TANGAN =====")

        val boundingBoxes = findWritingAreas(bitmap)

        val result = StringBuilder()

        if (boundingBoxes.isEmpty()) {
            Log.e(TAG, "Tidak ada area tulisan yang terdeteksi")
            return ""
        }

        Log.d(TAG, "Menemukan ${boundingBoxes.size} area tulisan")

        val sortedBoxes = boundingBoxes.sortedBy { it.left }

        for ((index, box) in sortedBoxes.withIndex()) {
            Log.d(TAG, "Memproses area #${index+1}: [${box.left},${box.top},${box.right},${box.bottom}]")

            val characterBitmap = extractCharacter(bitmap, box)

            if (box.width() > box.height() * 2.0 && box.width() > 80) {
                val segments = segmentConnectedChars(characterBitmap)

                if (segments.isNotEmpty()) {
                    Log.d(TAG, "Area besar disegmentasi menjadi ${segments.size} bagian")
                    for (segment in segments) {
                        val charResult = recognizeCharacter(segment)
                        result.append(charResult)
                    }
                } else {
                    val charResult = recognizeCharacter(characterBitmap)
                    result.append(charResult)
                }
            } else {
                val charResult = recognizeCharacter(characterBitmap)
                result.append(charResult)
            }
        }

        Log.d(TAG, "Hasil akhir pengenalan: '$result'")
        Log.d(TAG, "===== PENGENALAN TULISAN TANGAN SELESAI =====")
        return result.toString()
    }

    private fun findWritingAreas(bitmap: Bitmap): List<Rect> {
        val binaryBitmap = binarizeImage(bitmap)

        logDebugImage(binaryBitmap, "binarized")

        val width = binaryBitmap.width
        val height = binaryBitmap.height

        val visited = BooleanArray(width * height) { false }

        val areas = mutableListOf<Rect>()

        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1),               Pair(0, 1),
            Pair(1, -1),  Pair(1, 0),  Pair(1, 1)
        )

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x

                if (!visited[idx] && binaryBitmap[x, y] == Color.BLACK) {
                    var minX = x
                    var minY = y
                    var maxX = x
                    var maxY = y

                    // breadth-first search algorithm
                    val queue: Queue<Pair<Int, Int>> = LinkedList()
                    queue.add(Pair(x, y))
                    visited[idx] = true

                    while (queue.isNotEmpty()) {
                        val (curX, curY) = queue.remove()

                        minX = minOf(minX, curX)
                        minY = minOf(minY, curY)
                        maxX = maxOf(maxX, curX)
                        maxY = maxOf(maxY, curY)

                        for ((dx, dy) in directions) {
                            val nx = curX + dx
                            val ny = curY + dy

                            if (nx in 0 until width && ny in 0 until height) {
                                val nIdx = ny * width + nx

                                if (!visited[nIdx] && binaryBitmap[nx, ny] == Color.BLACK) {
                                    visited[nIdx] = true
                                    queue.add(Pair(nx, ny))
                                }
                            }
                        }
                    }

                    val rect = Rect(minX, minY, maxX + 1, maxY + 1)

                    if (rect.width() >= 10 && rect.height() >= 10) {
                        areas.add(rect)
                    }
                }
            }
        }

        return areas
    }

    private fun binarizeImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val result = createBitmap(width, height)

        val canvas = Canvas(result)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                setSaturation(0f)
            })
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val gray = Color.red(pixels[i])

            pixels[i] = if (gray > THRESHOLD) Color.WHITE else Color.BLACK
        }

        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    private fun extractCharacter(bitmap: Bitmap, rect: Rect): Bitmap {
        if (rect.width() <= 0 || rect.height() <= 0) {
            Log.e(TAG, "Bounding box tidak valid: $rect")
            return createEmptyBitmap(INPUT_SIZE)
        }

        val cropped = Bitmap.createBitmap(
            bitmap,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )

        return prepareForModel(cropped)
    }

    private fun segmentConnectedChars(bitmap: Bitmap): List<Bitmap> {
        val width = bitmap.width
        val height = bitmap.height
        val results = mutableListOf<Bitmap>()

        val projection = IntArray(width) { 0 }

        for (x in 0 until width) {
            var count = 0
            for (y in 0 until height) {
                if (bitmap[x, y] == Color.BLACK) {
                    count++
                }
            }
            projection[x] = count
        }

        val valleys = findValleys(projection)

        val segmentBoundaries = mutableListOf<Int>()
        segmentBoundaries.add(0)
        segmentBoundaries.addAll(valleys)
        segmentBoundaries.add(width - 1)

        Log.d(TAG, "Valleys ditemukan pada kolom: $valleys")

        for (i in 0 until segmentBoundaries.size - 1) {
            val start = segmentBoundaries[i]
            val end = segmentBoundaries[i + 1]

            if (end - start < 5) continue

            val segment = Bitmap.createBitmap(bitmap, start, 0, end - start, height)
            results.add(prepareForModel(segment))

            Log.d(TAG, "Attempting segment from $start to $end")
        }

        Log.d(TAG, "Projection profile for segmentation: ${projection.joinToString()}")

        return results
    }

    private fun findValleys(projection: IntArray): List<Int> {
        val valleys = mutableListOf<Int>()
        val width = projection.size

        val smoothed = smoothArray(projection, 3)

        for (x in 2 until width - 2) {
            if (smoothed[x] < smoothed[x-1] &&
                smoothed[x] < smoothed[x+1] &&
                smoothed[x] < smoothed[x-2] * 0.7 &&
                smoothed[x] < smoothed[x+2] * 0.7) {

                if (smoothed[x] < 0.2 * smoothed.average()) {
                    valleys.add(x)
                }
            }
        }

        if (valleys.size > 5) {
            valleys.sortBy { smoothed[it] }
            return valleys.take(5)
        }
        return valleys
    }

    private fun smoothArray(array: IntArray, windowSize: Int): IntArray {
        val result = IntArray(array.size)
        val halfWindow = windowSize / 2

        for (i in array.indices) {
            var sum = 0
            var count = 0

            for (j in maxOf(0, i - halfWindow)..minOf(array.size - 1, i + halfWindow)) {
                sum += array[j]
                count++
            }

            result[i] = sum / count
        }

        return result
    }

    private fun prepareForModel(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        var minX = width
        var minY = height
        var maxX = 0
        var maxY = 0
        var hasContent = false

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (bitmap[x, y] == Color.BLACK) {
                    hasContent = true
                    minX = minOf(minX, x)
                    minY = minOf(minY, y)
                    maxX = maxOf(maxX, x)
                    maxY = maxOf(maxY, y)
                }
            }
        }

        if (!hasContent) {
            return createEmptyBitmap(INPUT_SIZE)
        }

        val contentWidth = maxX - minX + 1
        val contentHeight = maxY - minY + 1

        val paddingX = (contentWidth * 0.1).toInt().coerceAtLeast(1)
        val paddingY = (contentHeight * 0.1).toInt().coerceAtLeast(1)

        val cropLeft = maxOf(0, minX - paddingX)
        val cropTop = maxOf(0, minY - paddingY)
        val cropRight = minOf(width, maxX + paddingX + 1)
        val cropBottom = minOf(height, maxY + paddingY + 1)

        val cropWidth = cropRight - cropLeft
        val cropHeight = cropBottom - cropTop

        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            cropLeft,
            cropTop,
            cropWidth,
            cropHeight
        )

        val result = createEmptyBitmap(INPUT_SIZE)

        val scale = minOf(
            INPUT_SIZE.toFloat() / cropWidth,
            INPUT_SIZE.toFloat() / cropHeight
        )

        val scaledWidth = (cropWidth * scale).toInt()
        val scaledHeight = (cropHeight * scale).toInt()

        val scaledBitmap = croppedBitmap.scale(scaledWidth, scaledHeight)

        val canvas = Canvas(result)
        val left = (INPUT_SIZE - scaledWidth) / 2f
        val top = (INPUT_SIZE - scaledHeight) / 2f
        canvas.drawBitmap(scaledBitmap, left, top, null)

        logDebugImage(result, "prepared")

        return result
    }

    private fun createEmptyBitmap(size: Int): Bitmap {
        return createBitmap(size, size).apply {
            eraseColor(Color.WHITE)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun recognizeCharacter(bitmap: Bitmap): String {
        val inputBitmap = if (bitmap.width != INPUT_SIZE || bitmap.height != INPUT_SIZE) {
            bitmap.scale(INPUT_SIZE, INPUT_SIZE)
        } else {
            bitmap
        }

        val inputBuffer = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        val inputValues = Array(INPUT_SIZE) { FloatArray(INPUT_SIZE) }

        for (y in 0 until INPUT_SIZE) {
            for (x in 0 until INPUT_SIZE) {
                val pixel = inputBitmap[x, y]
                val grayScale = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3f
                val normalized = 1.0f - (grayScale / 255.0f)

                inputBuffer.putFloat(normalized)
                inputValues[y][x] = normalized
            }
        }

        Log.d(TAG, "Sampel nilai input model (tengah 5x5):")
        for (y in 11..15) {
            val row = inputValues[y].slice(11..15).joinToString(" ") {
                String.format("%.2f", it)
            }
            Log.d(TAG, row)
        }

        val outputArray = Array(1) { FloatArray(47) }

        inputBuffer.rewind()
        tflite.run(inputBuffer, outputArray)

        val outputs = outputArray[0]
        var maxIndex = -1
        var maxConfidence = 0.0f

        for (i in outputs.indices) {
            if (outputs[i] > maxConfidence) {
                maxConfidence = outputs[i]
                maxIndex = i
            }
        }

        val topPredictions = outputs.withIndex()
            .sortedByDescending { it.value }
            .take(3)
            .map { indexed ->
                val char = labelMapping[indexed.index] ?: "?"
                val confidence = indexed.value * 100
                "$char (${String.format("%.1f", confidence)}%)"
            }

        Log.d(TAG, "Top 3 prediksi: ${topPredictions.joinToString(", ")}")

        val predictedChar = if (maxIndex >= 0 && maxIndex in labelMapping) {
            labelMapping[maxIndex] ?: "?"
        } else {
            "?"
        }

        Log.d(TAG, "Karakter terdeteksi: '$predictedChar' dengan confidence: ${String.format("%.1f", maxConfidence * 100)}%")

        return predictedChar
    }

    private fun logDebugImage(bitmap: Bitmap, label: String) {
        try {
            Log.d(TAG, "Debug image '$label': ${bitmap.width}x${bitmap.height}")

            val sb = StringBuilder()
            sb.appendLine("Sample pixels for '$label' (X = black pixel, . = white):")

            val step = maxOf(1, bitmap.height / 15)
            for (y in 0 until bitmap.height step step) {
                for (x in 0 until bitmap.width step step) {
                    val pixel = bitmap[x, y]
                    val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                    val symbol = if (gray < 128) "X" else "."
                    sb.append(symbol)
                }
                sb.appendLine()
            }

            Log.d(TAG, sb.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error logging debug image: ${e.message}")
        }
    }
}