package com.nara.bacayuk.writing.quiz.tracing

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityQuizAttemptBinding
import com.nara.bacayuk.writing.quiz.predict.HandwritingProcessor
import com.nara.bacayuk.writing.quiz.predict.PredictActivity
import com.nara.bacayuk.writing.quiz.result.QuizResultActivity
import com.nara.bacayuk.writing.quiz.question.Question
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream

class QuizAttemptActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizAttemptBinding
    private lateinit var viewModel: QuizAttemptViewModel
    private lateinit var quizSetId: String
    private var currentQuestionIndex = 0
    private var questions = listOf<Question>()
    private lateinit var tfLiteInterpreter: Interpreter
    private var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizAttemptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        quizSetId = intent.getStringExtra("quizSetId") ?: run {
            Toast.makeText(this, "Quiz Set ID tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnNext.setOnClickListener {
            setupClickListeners()
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
        viewModel.loadQuestions(quizSetId)
    }

    private fun setupClickListeners() {
        val userAnswer =  processDrawingAndPredict(binding.tracingCanvas.getBitmap())
        viewModel.saveAnswer(currentQuestionIndex, userAnswer)

        val bitmap = binding.tracingCanvas.getBitmap()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val intent = Intent(this, PredictActivity::class.java)
        intent.putExtra("userAnswer", userAnswer)
        intent.putExtra("tracingBitmap", byteArray)
        intent.putExtra("correctAnswer", questions[currentQuestionIndex].correctAnswer)
        startActivityForResult(intent, PREDICTION_REQUEST_CODE)
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.questions.observe(this) { questionList ->
            questions = questionList
            if (questions.isNotEmpty()) {
                showQuestion(currentQuestionIndex)
            }
        }

        viewModel.currentAttempt.observe(this) {
            binding.progressIndicator.max = questions.size
            binding.progressIndicator.progress = currentQuestionIndex + 1
            binding.tvProgress.text = "${currentQuestionIndex + 1}/${questions.size}"
        }
    }

    private fun showQuestion(index: Int) {
        if (index < questions.size) {
            val question = questions[index]
            binding.tvTitle.text = question.question
            binding.tracingCanvas.clearCanvas()
            binding.btnNext.text = if (index == questions.size - 1) "Selesai" else "Selanjutnya"
        }
    }

    private fun loadTFLiteModel() {
        try {
            Log.d("TracingQuizActivity", "Mencoba memuat model dari assets...")
            val tfliteModel = FileUtil.loadMappedFile(this, "emnist_model_optimized(3).tflite")
            tfLiteInterpreter = Interpreter(tfliteModel)
            Log.d("TracingQuizActivity", "Model berhasil dimuat")
        } catch (e: Exception) {
            Log.e("TracingQuizActivity", "Gagal memuat model: ${e.message}")
            throw RuntimeException("Gagal memuat model TFLite", e)
        }
    }

    private fun processDrawingAndPredict(bitmap: Bitmap?): String {
        val bitmapToProcess = bitmap ?: binding.tracingCanvas.getBitmap()
        val processor = HandwritingProcessor(this, tfLiteInterpreter)
        return processor.processImage(bitmapToProcess)
    }

    override fun onDestroy() {
        tfLiteInterpreter.close()
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PREDICTION_REQUEST_CODE && resultCode == RESULT_OK) {
            currentQuestionIndex++
            if (currentQuestionIndex >= questions.size) {
                val intent = Intent(this, QuizResultActivity::class.java)
                intent.putExtra("quizSetId", quizSetId)
                startActivity(intent)
                finish()
            } else {
                showQuestion(currentQuestionIndex)
            }
        }
    }

    companion object {
        private const val PREDICTION_REQUEST_CODE = 100
    }
}