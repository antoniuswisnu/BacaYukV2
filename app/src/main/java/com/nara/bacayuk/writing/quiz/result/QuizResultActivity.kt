package com.nara.bacayuk.writing.quiz.result

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityQuizResultBinding
import com.nara.bacayuk.writing.quiz.menu.MenuQuizActivity

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
            intent.getParcelableExtra("student") as Student?
        }

        binding.btnFinish.setOnClickListener{
            startActivity(Intent(this, MenuQuizActivity::class.java))
            finish()
        }

        val predictionResults = intent.getStringArrayListExtra("PREDICTION_RESULTS") ?: arrayListOf()
        val correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)
        val wrongAnswers = intent.getIntExtra("WRONG_ANSWERS", 0)

        binding.tvCorrectAnswers.text = "Benar: $correctAnswers"
        binding.tvWrongAnswers.text = "Salah: $wrongAnswers"

        val recyclerView: RecyclerView = findViewById(R.id.rv_results)
        val adapter = ResultAdapter(predictionResults)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}