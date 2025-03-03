package com.example.tracingalphabet.quiz.question

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tracingalphabet.databinding.ActivityListQuestionBinding

class ListQuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListQuestionBinding
    private lateinit var viewModel: QuizViewModel
    private lateinit var adapter: QuestionAdapter
    private lateinit var quizSetId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        quizSetId = intent.getStringExtra("quizSetId") ?: run {
            Toast.makeText(this, "Quiz Set ID tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("AddEditQuizActivity", "QuizSetId: $quizSetId")

        val title = intent.getStringExtra("title")
        binding.tvTitle.text = title

        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        viewModel.loadQuizzes(quizSetId)
    }

    private fun setupRecyclerView() {
        adapter = QuestionAdapter(
            onDeleteClick = { quiz ->
                viewModel.deleteQuiz(quiz.id)
            }
        )
        binding.rvListQuestion.apply {
            layoutManager = LinearLayoutManager(this@ListQuestionActivity)
            adapter = this@ListQuestionActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnAddQuestion.setOnClickListener {
            val intent = Intent(this, AddEditQuestionActivity::class.java).apply {
                putExtra("quizSetId", quizSetId)
            }
            Log.d("QuizListActivity", "Sending quizSetId: $quizSetId")
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        viewModel.quizzes.observe(this) { quizzes ->
            adapter.submitList(quizzes)
            binding.tvEmptyState.visibility = if (quizzes.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}