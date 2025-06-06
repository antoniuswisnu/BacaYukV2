package com.nara.bacayuk.writing.quiz.tracing

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModelProvider
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityQuizAttemptBinding
import com.nara.bacayuk.writing.quiz.predict.DigitalInkRecognizerHelper
import com.nara.bacayuk.writing.quiz.predict.HandwritingProcessor
import com.nara.bacayuk.writing.quiz.predict.PredictActivity
import com.nara.bacayuk.writing.quiz.result.QuizResultActivity
import com.nara.bacayuk.writing.quiz.question.Question
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream
import androidx.lifecycle.lifecycleScope
import com.nara.bacayuk.writing.quiz.predict.GeminiHelper
import kotlinx.coroutines.launch

class QuizAttemptActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizAttemptBinding
    private lateinit var viewModel: QuizAttemptViewModel
    private lateinit var quizSetId: String
    private var currentQuestionIndex = 0
    private var questions = listOf<Question>()
    private lateinit var tfLiteInterpreter: Interpreter
    private var student: Student? = null
    private lateinit var geminiHelper: GeminiHelper
    private lateinit var digitalInkHelper: DigitalInkRecognizerHelper

    private val attemptDetails = mutableListOf<QuizAttemptDetail>()
    private var correctAnswersCount = 0
    private var wrongAnswersCount = 0

    private val predictActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                proceedToNextQuestionOrFinish()
            } else {
                Log.d("QuizAttemptActivity", "PredictActivity did not return RESULT_OK")
                proceedToNextQuestionOrFinish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizAttemptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("student") as Student?
        }

        quizSetId = intent.getStringExtra("quizSetId") ?: run {
            Toast.makeText(this, "Quiz Set ID tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        geminiHelper = GeminiHelper(this)
        digitalInkHelper = DigitalInkRecognizerHelper()

        binding.btnNext.setOnClickListener {
            val currentQuestion = questions[currentQuestionIndex]
            val userBitmap = binding.tracingCanvas.getBitmap()
            if (currentQuestion.questionType == "Kata") {
                val strokes = binding.tracingCanvas.getStrokes()
                digitalInkHelper.recognize(strokes,
                    onResult = { predicted ->
                        handlePredictionResult(userBitmap, predicted)
                    },
                    onError = { e ->
                        Toast.makeText(this, "Gagal mengenali tulisan: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("QuizAttemptActivity", "Error recognizing handwriting: ${e.message}", e)
                    }
                )
            } else {
                val predicted = processDrawingAndPredict(userBitmap)
                handlePredictionResult(userBitmap, predicted)
            }
        }

        binding.btnReload.setOnClickListener {
            binding.tracingCanvas.clearCanvas()
        }

        binding.btnPencil.setOnClickListener {
            binding.btnPencil.setImageResource(R.drawable.ic_pencil_active)
            binding.tracingCanvas.visibility = View.VISIBLE
        }

        setupViewModel()
        observeViewModel()
        loadTFLiteModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[QuizAttemptViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.questions.observe(this) { questionList ->
            questions = questionList
            if (questions.isNotEmpty()) {
                binding.progressIndicator.max = questions.size
                showQuestion(currentQuestionIndex)
            } else {
                Toast.makeText(this, "Tidak ada pertanyaan dalam kuis ini.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
        viewModel.loadQuestions(quizSetId)

    }

    @SuppressLint("SetTextI18n")
    private fun showQuestion(index: Int) {
        if (index < questions.size) {
            val question = questions[index]
            binding.tvTitle.text = question.question
            binding.tracingCanvas.clearCanvas()
            binding.progressIndicator.progress = index + 1
            binding.tvProgress.text = "${index + 1}/${questions.size}"
            binding.btnNext.text = if (index == questions.size - 1) "Selesai" else "Periksa Jawaban"
        }
    }

    private fun loadTFLiteModel() {
        try {
            Log.d("QuizAttemptActivity", "Mencoba memuat model dari assets...")
            val tfliteModel = FileUtil.loadMappedFile(this, "emnist_model_optimized(3).tflite")
            tfLiteInterpreter = Interpreter(tfliteModel)
            Log.d("QuizAttemptActivity", "Model berhasil dimuat")
        } catch (e: Exception) {
            Log.e("QuizAttemptActivity", "Gagal memuat model: ${e.message}", e)
            Toast.makeText(this, "Gagal memuat model AI.", Toast.LENGTH_LONG).show()
        }
    }

    private fun processDrawingAndPredict(bitmap: Bitmap?): String {
        val bitmapToProcess = bitmap ?: binding.tracingCanvas.getBitmap()
        if (!::tfLiteInterpreter.isInitialized) {
            Log.e("QuizAttemptActivity", "Interpreter TFLite belum diinisialisasi.")
            return ""
        }
        val processor = HandwritingProcessor(this, tfLiteInterpreter)
        val predictedString = processor.processImage(bitmapToProcess)

//        if (predictedString.length > 5) {
//            predictedString = predictedString.substring(0, 5)
//            Log.d("QuizAttemptActivity", "Hasil prediksi dipotong menjadi 5 huruf: $predictedString")
//        }
//
        return predictedString
    }

    private fun handlePredictionResult(userAnswerBitmap: Bitmap, predictedAnswer: String) {
        if (predictedAnswer.isEmpty() && userAnswerBitmap.width > 0 && userAnswerBitmap.height > 0) {
            Toast.makeText(this, "Tidak ada tulisan terdeteksi. Coba tulis lebih jelas.", Toast.LENGTH_SHORT).show()
            return
        }
        if (predictedAnswer.isEmpty() && (userAnswerBitmap.width == 0 || userAnswerBitmap.height == 0)) {
            Toast.makeText(this, "Silakan tulis jawaban Anda terlebih dahulu.", Toast.LENGTH_SHORT).show()
            return
        }
        val current = questions[currentQuestionIndex]
        val isCorrect = predictedAnswer.equals(current.correctAnswer, ignoreCase = true)
        if (isCorrect) correctAnswersCount++ else wrongAnswersCount++
        attemptDetails.add(QuizAttemptDetail(
            questionText = current.question,
            correctAnswer = current.correctAnswer,
            userAnswerPredicted = predictedAnswer,
            isCorrect = isCorrect
        ))
        viewModel.saveAnswer(currentQuestionIndex, predictedAnswer)
        val baos = ByteArrayOutputStream().apply { userAnswerBitmap.compress(Bitmap.CompressFormat.PNG, 100, this) }
        val byteArray = baos.toByteArray()
        Intent(this, PredictActivity::class.java).apply {
            putExtra("userAnswer", predictedAnswer)
            putExtra("tracingBitmap", byteArray)
            putExtra("correctAnswer", current.correctAnswer)
            putExtra("student", student)
            putExtra("questionType", current.questionType)
        }.also { predictActivityLauncher.launch(it) }
        Log.d("GeminiHelper", "Mengirim ke PredictActivity: userAnswer=$predictedAnswer, correctAnswer=${current.correctAnswer}, questionType=${current.questionType}")
    }

    private fun proceedToNextQuestionOrFinish() {
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            showQuestion(currentQuestionIndex)
        } else {
            val intent = Intent(this, QuizResultActivity::class.java).apply {
                putExtra("quizSetId", quizSetId)
                putParcelableArrayListExtra("ATTEMPT_DETAILS", ArrayList(attemptDetails))
                putExtra("CORRECT_ANSWERS_COUNT", correctAnswersCount)
                putExtra("WRONG_ANSWERS_COUNT", wrongAnswersCount)
                putExtra("TOTAL_QUESTIONS", questions.size)
                putExtra("student", student)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        if (::tfLiteInterpreter.isInitialized) {
            tfLiteInterpreter.close()
        }
        super.onDestroy()
    }

    companion object {
    }
}