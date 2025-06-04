package com.nara.bacayuk.writing.quiz.result

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityQuizResultBinding
import com.nara.bacayuk.writing.quiz.menu.MenuQuizActivity
import com.nara.bacayuk.writing.quiz.tracing.QuizAttemptDetail

class QuizResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizResultBinding
    private var student: Student? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("student") as Student?
        }

        binding.btnFinish.setOnClickListener {
            val intent = Intent(this, MenuQuizActivity::class.java).apply {
                putExtra("student", student)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        val attemptDetails: ArrayList<QuizAttemptDetail>? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra("ATTEMPT_DETAILS", QuizAttemptDetail::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra("ATTEMPT_DETAILS")
            }

        val correctAnswersCount = intent.getIntExtra("CORRECT_ANSWERS_COUNT", 0)
        val wrongAnswersCount = intent.getIntExtra("WRONG_ANSWERS_COUNT", 0)
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", attemptDetails?.size ?: 0)

        if (attemptDetails == null || attemptDetails.isEmpty()) {
            Log.e("QuizResultActivity", "Tidak ada detail hasil kuis yang diterima.")
            binding.tvCorrectAnswers.text = "Benar: -"
            binding.tvWrongAnswers.text = "Salah: -"
            binding.tvScore.text = "Skor: 0%"
        } else {
            Log.d("QuizResultActivity", "Menerima ${attemptDetails.size} detail hasil.")
            binding.tvCorrectAnswers.text = "Benar: $correctAnswersCount"
            binding.tvWrongAnswers.text = "Salah: $wrongAnswersCount"

            val score = if (totalQuestions > 0) {
                (correctAnswersCount.toDouble() / totalQuestions.toDouble() * 100).toInt()
            } else {
                0
            }
            binding.tvScore.text = "Skor: $score%"

            val adapter = ResultAdapter(attemptDetails)
            binding.rvResults.adapter = adapter
            binding.rvResults.layoutManager = LinearLayoutManager(this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MenuQuizActivity::class.java).apply {
            putExtra("student", student)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }
}
