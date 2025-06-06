package com.nara.bacayuk.writing.quiz.predict

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.nara.bacayuk.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.scale

class GeminiHelper(private val context: Context) {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val isGooglePlayServicesAvailable: Boolean
        get() {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
            return resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS
        }

    private val generativeModel by lazy {
        if (!isGooglePlayServicesAvailable) {
            Log.e("GeminiHelper", "Google Play Services tidak tersedia")
            throw GooglePlayServicesNotAvailableException(0)
        }

        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
    }

    suspend fun analyzeHandwriting(
        bitmap: Bitmap,
        predictedChar: String,
        correctChar: String
    ): String = withContext(Dispatchers.IO) {
        try {
            if (!isGooglePlayServicesAvailable) {
                return@withContext getFallbackFeedback(predictedChar, correctChar)
            }

            val isCorrect = predictedChar.equals(correctChar, ignoreCase = true)

            if (isCorrect) {
                return@withContext "Hebat! Tulisan \"$correctChar\" Anda sudah benar. Teruskan latihan untuk memperlancar penulisan."
            }

            val scaledBitmap = scaleBitmap(bitmap, 512)

            val prompt = """
                Analisis gambar tulisan tangan yang disediakan. Ini adalah upaya penulisan huruf "$correctChar".
                Sistem mendeteksi tulisan sebagai "$predictedChar" bukan "$correctChar".
                
                Berikan umpan balik yang ramah dan mendidik untuk anak-anak tentang:
                1. Apa yang membuat tulisan tersebut terlihat seperti "$predictedChar"
                2. Bagaimana cara memperbaiki tulisan agar lebih terlihat seperti "$correctChar"
                3. Berikan saran praktis dan spesifik (misalnya tentang bentuk, garis, atau proporsi)
                
                Berikan respons dalam bahasa Indonesia dengan nada positif dan semangat. 
                Maksimal 3 kalimat pendek dan sederhana untuk anak-anak.
            """.trimIndent()

            val response = generativeModel.generateContent(
                content {
                    image(scaledBitmap)
                    text(prompt)
                }
            )

            return@withContext response.text?.trim()?.ifEmpty {
                getFallbackFeedback(predictedChar, correctChar)
            } ?: getFallbackFeedback(predictedChar, correctChar)

        } catch (e: Exception) {
            Log.e("GeminiHelper", "Error saat menganalisis tulisan: ${e.message}", e)
            return@withContext getFallbackFeedback(predictedChar, correctChar)
        }
    }

    private fun scaleBitmap(original: Bitmap, maxDimension: Int): Bitmap {
        val width = original.width
        val height = original.height

        if (width <= maxDimension && height <= maxDimension) {
            return original
        }

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxDimension
            newHeight = (newWidth / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (newHeight * ratio).toInt()
        }

        return original.scale(newWidth, newHeight)
    }

    private fun getFallbackFeedback(predictedChar: String, correctChar: String): String {
        val isCorrect = predictedChar.equals(correctChar, ignoreCase = true)

        return if (isCorrect) {
            "Hebat! Tulisan \"$correctChar\" Anda sudah benar."
        } else {
            "Coba tulis \"$correctChar\" dengan lebih hati-hati. Perhatikan bentuk dan garisnya."
        }
    }
}

