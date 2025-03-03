package com.example.tracingalphabet.quiz.predict

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracingalphabet.R
import com.example.tracingalphabet.databinding.ActivityQuizResultBinding
import com.example.tracingalphabet.quiz.menu.MenuQuizActivity

class QuizResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

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