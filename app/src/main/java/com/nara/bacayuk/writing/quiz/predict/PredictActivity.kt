package com.nara.bacayuk.writing.quiz.predict

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityPredictBinding
import kotlinx.coroutines.launch

class PredictActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPredictBinding
    private var geminiFeedbackHelper: GeminiHelper? = null
    private var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        val userAnswer = intent.getStringExtra("userAnswer") ?: ""
        val correctAnswer = intent.getStringExtra("correctAnswer") ?: ""
        val byteArray = intent.getByteArrayExtra("tracingBitmap")
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)

        binding.imgTracing.setImageBitmap(bitmap)
        binding.tvPredictionResult.text = userAnswer
        binding.tvCorrectAnswer.text = correctAnswer
        binding.tvFeedback.visibility = View.GONE

        try {
            geminiFeedbackHelper = GeminiHelper(this)

            checkGooglePlayServices()

            lifecycleScope.launch {
                try {
                    val feedback = geminiFeedbackHelper?.analyzeHandwriting(
                        bitmap = bitmap,
                        predictedChar = userAnswer,
                        correctChar = correctAnswer
                    ) ?: getFallbackFeedback(userAnswer, correctAnswer)

                    binding.tvFeedback.visibility = View.VISIBLE
                    binding.tvFeedback.text = feedback
                } catch (e: Exception) {
                    Log.e("PredictActivity", "Error saat mendapatkan umpan balik: ${e.message}", e)
                    handleFeedbackError(userAnswer, correctAnswer)
                }
            }
        } catch (e: Exception) {
            Log.e("PredictActivity", "Error saat inisialisasi GeminiHelper: ${e.message}", e)
            handleFeedbackError(userAnswer, correctAnswer)
        }

        binding.btnContinue.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 9000)?.show()
            } else {
                Log.e("PredictActivity", "Perangkat ini tidak mendukung Google Play Services")
            }
            throw GooglePlayServicesNotAvailableException(resultCode)
        }
    }

    private fun handleFeedbackError(userAnswer: String, correctAnswer: String) {
        binding.tvFeedback.visibility = View.VISIBLE
        binding.tvFeedback.text = getFallbackFeedback(userAnswer, correctAnswer)
    }

    private fun getFallbackFeedback(userAnswer: String, correctAnswer: String): String {
        val isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)
        return if (isCorrect)
            "Benar! Tulisan \"$correctAnswer\" Anda sudah bagus."
        else
            "Belum tepat. Jawaban yang benar: $correctAnswer. Coba perhatikan bentuk hurufnya."
    }
}