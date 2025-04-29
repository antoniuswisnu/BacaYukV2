package com.nara.bacayuk.writing.quiz.predict

import android.content.Context
import android.graphics.*
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.tensorflow.lite.Interpreter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set
import java.util.*

/**
 * HandwritingProcessor - kelas untuk memproses tulisan tangan dan mengenali karakter
 * Dirancang khusus untuk inferensi model TFLite pada tulisan tangan langsung di layar Android
 */
class HandwritingProcessor(private val context: Context, private val tflite: Interpreter) {

    companion object {
        private const val TAG = "HandwritingProcessor"
        private const val INPUT_SIZE = 28 // Model memerlukan input 28x28
        private const val THRESHOLD = 200 // Threshold untuk binarisasi gambar (0-255)
    }

    // Mapping antara indeks output model dan karakter yang sesuai (0-9, A-Z)
    private val labelMapping = mapOf(
        0 to "0", 1 to "1", 2 to "2", 3 to "3", 4 to "4",
        5 to "5", 6 to "6", 7 to "7", 8 to "8", 9 to "9",
        10 to "A", 11 to "B", 12 to "C", 13 to "D", 14 to "E",
        15 to "F", 16 to "G", 17 to "H", 18 to "I", 19 to "J",
        20 to "K", 21 to "L", 22 to "M", 23 to "N", 24 to "O",
        25 to "P", 26 to "Q", 27 to "R", 28 to "S", 29 to "T",
        30 to "U", 31 to "V", 32 to "W", 33 to "X", 34 to "Y",
        35 to "Z"
    )

    /**
     * Fungsi utama untuk memproses gambar dan mengenali tulisan tangan
     */
    fun processImage(bitmap: Bitmap): String {
        Log.d(TAG, "===== MEMULAI PENGENALAN TULISAN TANGAN =====")

        // 1. Cari area tulisan tangan (bounding boxes)
        val boundingBoxes = findWritingAreas(bitmap)

        // 2. Proses setiap area tulisan
        val result = StringBuilder()

        if (boundingBoxes.isEmpty()) {
            Log.e(TAG, "Tidak ada area tulisan yang terdeteksi")
            return ""
        }

        Log.d(TAG, "Menemukan ${boundingBoxes.size} area tulisan")

        // Urutkan bounding box dari kiri ke kanan
        val sortedBoxes = boundingBoxes.sortedBy { it.left }

        // Proses setiap area yang ditemukan
        for ((index, box) in sortedBoxes.withIndex()) {
            Log.d(TAG, "Memproses area #${index+1}: [${box.left},${box.top},${box.right},${box.bottom}]")

            // Ekstrak area tulisan
            val characterBitmap = extractCharacter(bitmap, box)

            // Jika area terlalu besar, mungkin itu adalah beberapa karakter yang terhubung
            if (box.width() > box.height() * 1.5 && box.width() > 50) {
                // Coba segmentasi lebih lanjut
                val segments = segmentConnectedChars(characterBitmap)

                if (segments.isNotEmpty()) {
                    Log.d(TAG, "Area besar disegmentasi menjadi ${segments.size} bagian")
                    for (segment in segments) {
                        val charResult = recognizeCharacter(segment)
                        result.append(charResult)
                    }
                } else {
                    // Jika segmentasi gagal, proses sebagai satu karakter
                    val charResult = recognizeCharacter(characterBitmap)
                    result.append(charResult)
                }
            } else {
                // Proses sebagai satu karakter
                val charResult = recognizeCharacter(characterBitmap)
                result.append(charResult)
            }
        }

        Log.d(TAG, "Hasil akhir pengenalan: '$result'")
        Log.d(TAG, "===== PENGENALAN TULISAN TANGAN SELESAI =====")
        return result.toString()
    }

    /**
     * Menemukan area tulisan dalam gambar
     */
    private fun findWritingAreas(bitmap: Bitmap): List<Rect> {
        // Konversi gambar ke biner (hitam dan putih)
        val binaryBitmap = binarizeImage(bitmap)

        // Log sampel gambar biner untuk debug
        logDebugImage(binaryBitmap, "binarized")

        val width = binaryBitmap.width
        val height = binaryBitmap.height

        // Array untuk menandai piksel yang sudah dikunjungi
        val visited = BooleanArray(width * height) { false }

        // List untuk menyimpan area tulisan
        val areas = mutableListOf<Rect>()

        // Arah pencarian (8 arah)
        val directions = arrayOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1),               Pair(0, 1),
            Pair(1, -1),  Pair(1, 0),  Pair(1, 1)
        )

        // Cari komponen terhubung (connected components)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x

                // Jika piksel hitam dan belum dikunjungi
                if (!visited[idx] && binaryBitmap[x, y] == Color.BLACK) {
                    // Inisialisasi bounding box baru
                    var minX = x
                    var minY = y
                    var maxX = x
                    var maxY = y

                    // Gunakan breadth-first search untuk menemukan seluruh komponen
                    val queue: Queue<Pair<Int, Int>> = LinkedList()
                    queue.add(Pair(x, y))
                    visited[idx] = true

                    while (queue.isNotEmpty()) {
                        val (curX, curY) = queue.remove()

                        // Update bounding box
                        minX = minOf(minX, curX)
                        minY = minOf(minY, curY)
                        maxX = maxOf(maxX, curX)
                        maxY = maxOf(maxY, curY)

                        // Periksa 8 arah tetangga
                        for ((dx, dy) in directions) {
                            val nx = curX + dx
                            val ny = curY + dy

                            // Pastikan koordinat valid
                            if (nx in 0 until width && ny in 0 until height) {
                                val nIdx = ny * width + nx

                                // Jika piksel hitam dan belum dikunjungi
                                if (!visited[nIdx] && binaryBitmap[nx, ny] == Color.BLACK) {
                                    visited[nIdx] = true
                                    queue.add(Pair(nx, ny))
                                }
                            }
                        }
                    }

                    // Buat Rect dari bounding box
                    val rect = Rect(minX, minY, maxX + 1, maxY + 1)

                    // Filter area yang terlalu kecil (kemungkinan noise)
                    if (rect.width() >= 10 && rect.height() >= 10) {
                        areas.add(rect)
                    }
                }
            }
        }

        return areas
    }

    /**
     * Mengubah gambar menjadi hitam-putih dengan threshold
     */
    private fun binarizeImage(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Buat bitmap baru untuk hasil
        val result = createBitmap(width, height)

        // Ubah ke grayscale terlebih dahulu
        val canvas = Canvas(result)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                setSaturation(0f) // Ubah ke grayscale
            })
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        // Binarisasi dengan threshold
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            // Ambil nilai grayscale (semua channel RGB sama dalam grayscale image)
            val gray = Color.red(pixels[i])

            // Apply threshold
            pixels[i] = if (gray > THRESHOLD) Color.WHITE else Color.BLACK
        }

        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    /**
     * Mengekstrak dan menyiapkan gambar karakter dari area tertentu
     */
    private fun extractCharacter(bitmap: Bitmap, rect: Rect): Bitmap {
        // Periksa validitas bounding box
        if (rect.width() <= 0 || rect.height() <= 0) {
            Log.e(TAG, "Bounding box tidak valid: $rect")
            return createEmptyBitmap(INPUT_SIZE)
        }

        // Potong gambar sesuai bounding box
        val cropped = Bitmap.createBitmap(
            bitmap,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )

        // Siapkan gambar untuk model (pusat dan ukur ulang)
        return prepareForModel(cropped)
    }

    /**
     * Mencoba segmentasi karakter yang mungkin terhubung
     */
    private fun segmentConnectedChars(bitmap: Bitmap): List<Bitmap> {
        val width = bitmap.width
        val height = bitmap.height
        val results = mutableListOf<Bitmap>()

        // Hitung proyeksi vertikal (jumlah piksel hitam per kolom)
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

        // Temukan lembah dalam proyeksi untuk memisahkan karakter
        val valleys = findValleys(projection)

        // Tambahkan awal dan akhir untuk membentuk segmen lengkap
        val segmentBoundaries = mutableListOf<Int>()
        segmentBoundaries.add(0)
        segmentBoundaries.addAll(valleys)
        segmentBoundaries.add(width - 1)

        // Log valleys untuk debugging
        Log.d(TAG, "Valleys ditemukan pada kolom: $valleys")

        // Buat segmen berdasarkan batas
        for (i in 0 until segmentBoundaries.size - 1) {
            val start = segmentBoundaries[i]
            val end = segmentBoundaries[i + 1]

            // Abaikan segmen yang terlalu sempit
            if (end - start < 5) continue

            // Potong dan siapkan segmen
            val segment = Bitmap.createBitmap(bitmap, start, 0, end - start, height)
            results.add(prepareForModel(segment))
        }

        return results
    }

    /**
     * Menemukan lembah dalam proyeksi (tempat potensial untuk memisahkan karakter)
     */
    private fun findValleys(projection: IntArray): List<Int> {
        val valleys = mutableListOf<Int>()
        val width = projection.size

        // Smoothing proyeksi untuk mengurangi noise
        val smoothed = smoothArray(projection, 3)

        // Temukan lembah signifikan
        for (x in 2 until width - 2) {
            // Lembah adalah tempat di mana nilai lebih rendah dari tetangga
            if (smoothed[x] < smoothed[x-1] &&
                smoothed[x] < smoothed[x+1] &&
                smoothed[x] < smoothed[x-2] * 0.7 &&
                smoothed[x] < smoothed[x+2] * 0.7) {

                // Tambahkan jika proyeksi cukup rendah (sedikit piksel hitam)
                if (smoothed[x] < 0.2 * smoothed.average()) {
                    valleys.add(x)
                }
            }
        }

        // Jika terlalu banyak valley, ambil yang paling signifikan saja
        if (valleys.size > 5) {
            valleys.sortBy { smoothed[it] }
            return valleys.take(5)
        }

        return valleys
    }

    /**
     * Melakukan smoothing pada array untuk mengurangi noise
     */
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

    /**
     * Menyiapkan gambar untuk input model
     * - Memusatkan karakter
     * - Mengubah ukuran menjadi 28x28
     * - Memastikan kontras yang baik
     */
    private fun prepareForModel(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Temukan batas karakter (area hitam)
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

        // Jika tidak ada konten, kembalikan bitmap kosong
        if (!hasContent) {
            return createEmptyBitmap(INPUT_SIZE)
        }

        // Potong ke area karakter dengan sedikit padding
        val contentWidth = maxX - minX + 1
        val contentHeight = maxY - minY + 1

        // Tambahkan padding 10%
        val paddingX = (contentWidth * 0.1).toInt().coerceAtLeast(1)
        val paddingY = (contentHeight * 0.1).toInt().coerceAtLeast(1)

        // Batasi area crop agar tidak keluar dari bitmap
        val cropLeft = maxOf(0, minX - paddingX)
        val cropTop = maxOf(0, minY - paddingY)
        val cropRight = minOf(width, maxX + paddingX + 1)
        val cropBottom = minOf(height, maxY + paddingY + 1)

        val cropWidth = cropRight - cropLeft
        val cropHeight = cropBottom - cropTop

        // Crop ke area konten
        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            cropLeft,
            cropTop,
            cropWidth,
            cropHeight
        )

        // Buat bitmap 28x28 dengan latar putih
        val result = createEmptyBitmap(INPUT_SIZE)

        // Hitung skala sambil mempertahankan rasio aspek
        val scale = minOf(
            INPUT_SIZE.toFloat() / cropWidth,
            INPUT_SIZE.toFloat() / cropHeight
        )

        val scaledWidth = (cropWidth * scale).toInt()
        val scaledHeight = (cropHeight * scale).toInt()

        // Resize karakter
        val scaledBitmap = Bitmap.createScaledBitmap(
            croppedBitmap,
            scaledWidth,
            scaledHeight,
            true
        )

        // Gambar karakter ke tengah bitmap hasil
        val canvas = Canvas(result)
        val left = (INPUT_SIZE - scaledWidth) / 2f
        val top = (INPUT_SIZE - scaledHeight) / 2f
        canvas.drawBitmap(scaledBitmap, left, top, null)

        // Log untuk debugging
        logDebugImage(result, "prepared")

        return result
    }

    /**
     * Membuat bitmap kosong dengan latar putih
     */
    private fun createEmptyBitmap(size: Int): Bitmap {
        return createBitmap(size, size).apply {
            eraseColor(Color.WHITE)
        }
    }

    /**
     * Mengenali karakter dari gambar yang sudah disiapkan
     */
    private fun recognizeCharacter(bitmap: Bitmap): String {
        // Pastikan ukuran input sesuai (28x28)
        val inputBitmap = if (bitmap.width != INPUT_SIZE || bitmap.height != INPUT_SIZE) {
            Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        } else {
            bitmap
        }

        // Persiapkan input buffer untuk model
        val inputBuffer = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        // Array untuk logging nilai input
        val inputValues = Array(INPUT_SIZE) { FloatArray(INPUT_SIZE) }

        // Isi buffer dengan nilai piksel ternormalisasi
        for (y in 0 until INPUT_SIZE) {
            for (x in 0 until INPUT_SIZE) {
                // Konversi piksel ke nilai float normalisasi
                // Nilai hitam (karakter) = 1.0f, nilai putih (latar) = 0.0f
                val pixel = inputBitmap[x, y]
                val grayScale = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3f
                val normalized = 1.0f - (grayScale / 255.0f)

                inputBuffer.putFloat(normalized)
                inputValues[y][x] = normalized
            }
        }

        // Log sampel nilai input untuk debugging
        Log.d(TAG, "Sampel nilai input model (tengah 5x5):")
        for (y in 11..15) {
            val row = inputValues[y].slice(11..15).joinToString(" ") {
                String.format("%.2f", it)
            }
            Log.d(TAG, row)
        }

        // Persiapkan output array
        val outputArray = Array(1) { FloatArray(36) } // 0-9, A-Z = 36 kelas

        // Jalankan inferensi
        inputBuffer.rewind()
        tflite.run(inputBuffer, outputArray)

        // Dapatkan prediksi dengan confidence tertinggi
        val outputs = outputArray[0]
        var maxIndex = -1
        var maxConfidence = 0.0f

        for (i in outputs.indices) {
            if (outputs[i] > maxConfidence) {
                maxConfidence = outputs[i]
                maxIndex = i
            }
        }

        // Dapatkan 3 prediksi teratas untuk logging
        val topPredictions = outputs.withIndex()
            .sortedByDescending { it.value }
            .take(3)
            .map { indexed ->
                val char = labelMapping[indexed.index] ?: "?"
                val confidence = indexed.value * 100 // Persentase
                "$char (${String.format("%.1f", confidence)}%)"
            }

        Log.d(TAG, "Top 3 prediksi: ${topPredictions.joinToString(", ")}")

        // Konversi index ke karakter
        val predictedChar = if (maxIndex >= 0 && maxIndex in labelMapping) {
            labelMapping[maxIndex] ?: "?"
        } else {
            "?"
        }

        Log.d(TAG, "Karakter terdeteksi: '$predictedChar' dengan confidence: ${String.format("%.1f", maxConfidence * 100)}%")

        return predictedChar
    }

    /**
     * Fungsi untuk logging gambar ke debug log
     */
    private fun logDebugImage(bitmap: Bitmap, label: String) {
        try {
            Log.d(TAG, "Debug image '$label': ${bitmap.width}x${bitmap.height}")

            val sb = StringBuilder()
            sb.appendLine("Sample pixels for '$label' (X = black pixel, . = white):")

            // Tampilkan representasi visual sederhana
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