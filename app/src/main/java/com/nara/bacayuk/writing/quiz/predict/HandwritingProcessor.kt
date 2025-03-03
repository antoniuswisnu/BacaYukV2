package com.example.tracingalphabet.quiz.predict

import android.content.Context
import android.graphics.*
import android.util.Log
import com.example.tracingalphabet.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class HandwritingProcessor(private val context: Context, private val tflite: Interpreter) {

    fun processImage(bitmap: Bitmap): String {
        // Ekstrak bounding box dari seluruh tulisan
        val boxes = extractBoundingBoxes(bitmap)
        val wordBuilder = StringBuilder()
        if (boxes.isEmpty()) {
            Log.e("HandwritingProcessor", "No bounding boxes detected.")
            return ""
        }
        // Jika lebih dari satu bounding box (misalnya, huruf terpisah)
        if (boxes.size > 1) {
            for (box in boxes) {
                val letterBitmap = extractLetterBitmap(bitmap, box)
                val letter = predictLetter(letterBitmap)
                wordBuilder.append(letter)
            }
        } else {
            // Hanya satu bounding box, asumsikan itu adalah kata atau kalimat yang tertulis
            val wordBox = boxes[0]
            val wordBitmap = Bitmap.createBitmap(bitmap, wordBox.left, wordBox.top, wordBox.width(), wordBox.height())
            // Segmentasi huruf menggunakan vertical projection
            val letterBitmaps = segmentLetters(wordBitmap)
            // Jika segmentasi gagal (tidak menemukan huruf terpisah), gunakan seluruh bounding box
            if(letterBitmaps.isEmpty()){
                val letter = predictLetter(Bitmap.createScaledBitmap(wordBitmap, 32, 32, true))
                wordBuilder.append(letter)
            } else {
                for (letterBmp in letterBitmaps) {
                    val letter = predictLetter(letterBmp)
                    wordBuilder.append(letter)
                }
            }
        }
        return wordBuilder.toString()
    }

    private fun extractBoundingBoxes(bitmap: Bitmap): List<Rect> {
        val width = bitmap.width
        val height = bitmap.height
        val binaryBitmap = convertToBinary(bitmap)

        val pixels = IntArray(width * height)
        binaryBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val visited = BooleanArray(width * height)
        val boundingBoxes = mutableListOf<Rect>()
        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1),              Pair(0, 1),
            Pair(1, -1),  Pair(1, 0),  Pair(1, 1)
        )

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                if (!visited[index] && pixels[index] == Color.BLACK) {
                    var minX = x
                    var minY = y
                    var maxX = x
                    var maxY = y
                    val queue: Queue<Pair<Int, Int>> = LinkedList()
                    queue.add(Pair(x, y))
                    visited[index] = true

                    while (queue.isNotEmpty()) {
                        val (cx, cy) = queue.remove()
                        if (cx < minX) minX = cx
                        if (cy < minY) minY = cy
                        if (cx > maxX) maxX = cx
                        if (cy > maxY) maxY = cy

                        for ((dx, dy) in directions) {
                            val nx = cx + dx
                            val ny = cy + dy
                            if (nx in 0 until width && ny in 0 until height) {
                                val nIndex = ny * width + nx
                                if (!visited[nIndex] && pixels[nIndex] == Color.BLACK) {
                                    visited[nIndex] = true
                                    queue.add(Pair(nx, ny))
                                }
                            }
                        }
                    }
                    val rect = Rect(minX, minY, maxX + 1, maxY + 1)
                    // Abaikan kotak kecil
                    if (rect.width() > 10 && rect.height() > 10) {
                        boundingBoxes.add(rect)
                    }
                }
            }
        }
        Log.d("HandwritingProcessor", "Total bounding boxes detected: ${boundingBoxes.size}")
        for ((index, rect) in boundingBoxes.withIndex()) {
            Log.d("HandwritingProcessor", "Bounding Box $index: left=${rect.left}, top=${rect.top}, width=${rect.width()}, height=${rect.height()}")
        }
        // Urutkan dari kiri ke kanan
        return boundingBoxes.sortedBy { it.left }
    }

    private fun convertToBinary(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val binaryBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(binaryBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f) // Konversi ke grayscale
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        // Sesuaikan threshold sesuai kondisi gambar
        val threshold = 150
        val pixels = IntArray(width * height)
        binaryBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in pixels.indices) {
            val gray = Color.red(pixels[i])
            pixels[i] = if (gray > threshold) Color.WHITE else Color.BLACK
        }
        binaryBitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return dilateImage(binaryBitmap)
    }

    private fun dilateImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val pixels = IntArray(width * height)
        outputBitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val tempPixels = pixels.clone()

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                var isBlack = false
                for (ky in -1..1) {
                    for (kx in -1..1) {
                        val pixel = tempPixels[(y + ky) * width + (x + kx)]
                        if (pixel == Color.BLACK) {
                            isBlack = true
                            break
                        }
                    }
                    if (isBlack) break
                }
                if (isBlack) pixels[y * width + x] = Color.BLACK
            }
        }
        outputBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return outputBitmap
    }

    private fun extractLetterBitmap(originalBitmap: Bitmap, rect: Rect): Bitmap {
        if (rect.width() <= 0 || rect.height() <= 0) {
            Log.e("HandwritingProcessor", "Invalid bounding box: $rect")
            return Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
        }
        val croppedBitmap = Bitmap.createBitmap(originalBitmap, rect.left, rect.top, rect.width(), rect.height())
        return Bitmap.createScaledBitmap(croppedBitmap, 32, 32, true)
    }

    private fun segmentLetters(wordBitmap: Bitmap): List<Bitmap> {
        val letters = mutableListOf<Bitmap>()
        val width = wordBitmap.width
        val height = wordBitmap.height
        val projection = IntArray(width) { 0 }

        // Hitung jumlah pixel hitam per kolom
        for (x in 0 until width) {
            var count = 0
            for (y in 0 until height) {
                if (wordBitmap.getPixel(x, y) == Color.BLACK) {
                    count++
                }
            }
            projection[x] = count
        }

        // Tentukan ambang batas untuk kolom kosong (sesuaikan threshold jika perlu)
        val threshold = 1
        val segments = mutableListOf<Pair<Int, Int>>()
        var inSegment = false
        var start = 0

        for (x in 0 until width) {
            if (projection[x] > threshold && !inSegment) {
                inSegment = true
                start = x
            }
            if ((projection[x] <= threshold || x == width - 1) && inSegment) {
                inSegment = false
                val end = if (x == width - 1 && projection[x] > threshold) x else x - 1
                segments.add(Pair(start, end))
            }
        }

        Log.d("HandwritingProcessor", "Segmented letter regions: ${segments.size}")
        for ((i, seg) in segments.withIndex()) {
            Log.d("HandwritingProcessor", "Segment $i: start=${seg.first}, end=${seg.second}")
        }

        for ((start, end) in segments) {
            if (end - start + 1 > 2) { // Minimal width agar dianggap valid
                val letterBmp = Bitmap.createBitmap(wordBitmap, start, 0, end - start + 1, height)
                val resized = Bitmap.createScaledBitmap(letterBmp, 32, 32, true)
                letters.add(resized)
            }
        }
        return letters
    }

    private fun centerLetter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var minX = width
        var minY = height
        var maxX = 0
        var maxY = 0

        // Cari bounding box huruf (pixel hitam)
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (bitmap.getPixel(x, y) == Color.BLACK) {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }
        // Jika tidak ditemukan pixel hitam, kembalikan bitmap kosong
        if (maxX < minX || maxY < minY) {
            return Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.WHITE)
            }
        }
        // Crop huruf
        val letterBitmap = Bitmap.createBitmap(bitmap, minX, minY, maxX - minX + 1, maxY - minY + 1)
        // Buat bitmap 32x32 dengan latar belakang putih
        val centeredBitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(centeredBitmap)
        canvas.drawColor(Color.WHITE)
        val offsetX = (32 - letterBitmap.width) / 2f
        val offsetY = (32 - letterBitmap.height) / 2f
        canvas.drawBitmap(letterBitmap, offsetX, offsetY, null)
        return centeredBitmap
    }

    private fun predictLetter(bitmap: Bitmap): String {
        // Lakukan centering huruf agar konsisten
        val centeredBitmap = centerLetter(bitmap)

        // Buat ByteBuffer untuk input model (32x32 float)
        val inputBuffer = ByteBuffer.allocateDirect(32 * 32 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until 32) {
            for (x in 0 until 32) {
                // Karena latar belakang putih dan tulisan hitam,
                // kita asumsikan bahwa nilai huruf = 1 - (grayscale/255)
                val pixel = centeredBitmap.getPixel(x, y)
                val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3f
                // Inversi: Tulisan hitam (0) menjadi 1, latar putih (255) menjadi 0
                val normalized = 1f - (gray / 255.0f)
                inputBuffer.putFloat(normalized)
            }
        }

        val output = Array(1) { FloatArray(35) }
//        val tfliteModel = FileUtil.loadMappedFile(this, "model.tflite")
        tflite.run(inputBuffer, output)
        val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1

        return if (predictedIndex in 0..25) {
            ('A' + predictedIndex).toString()
        } else {
            "?"
        }
    }

    private suspend fun getGemini(){
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.API_KEY
        )

//        val cookieImage: Bitmap =
        val inputContent = content() {
//            image(cookieImage)
            text("Berikan feedback mnengenai tulisan tangan tersebut")
        }

        val response = generativeModel.generateContent(inputContent)
        print(response.text)
    }
}
