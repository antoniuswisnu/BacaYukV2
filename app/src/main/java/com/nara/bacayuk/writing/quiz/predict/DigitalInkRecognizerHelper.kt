package com.nara.bacayuk.writing.quiz.predict

import android.graphics.PointF
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.Ink.Point
import com.google.mlkit.vision.digitalink.Ink.Stroke

class DigitalInkRecognizerHelper {

    private val modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("id")!!
    private val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
    private val options = DigitalInkRecognizerOptions.builder(model).build()
    private val recognizer: DigitalInkRecognizer = DigitalInkRecognition.getClient(options)
    private val remoteModelManager = RemoteModelManager.getInstance()

    fun recognize(
        strokes: List<List<PointF>>,
        onResult: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        remoteModelManager.download(model, DownloadConditions.Builder().build())
            .addOnSuccessListener {
                Log.i(TAG, "Model downloaded")
            }
            .addOnFailureListener { e: Exception ->
                Log.e(TAG, "Error while downloading a model: $e")
            }

        remoteModelManager.isModelDownloaded(model)

        val inkBuilder = Ink.builder()
        strokes.forEach { stroke ->
            val strokeBuilder = Stroke.builder()
            stroke.forEach { pt ->
                strokeBuilder.addPoint(Point.create(pt.x, pt.y, System.currentTimeMillis()))
            }
            inkBuilder.addStroke(strokeBuilder.build())
        }
        val ink = inkBuilder.build()
        recognizer.recognize(ink)
            .addOnSuccessListener { result ->
                val candidate = result.candidates.firstOrNull()?.text ?: ""
                Log.i(TAG, result.candidates[0].text)
                onResult(candidate)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error during recognition: $e")
                onError(e)
            }
    }

    companion object {
        private const val TAG = "DigitalInkRecognizerHelper"
    }
}
