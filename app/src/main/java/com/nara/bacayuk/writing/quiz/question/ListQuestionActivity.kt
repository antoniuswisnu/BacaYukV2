package com.nara.bacayuk.writing.quiz.question

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityListQuestionBinding
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.writing.quiz.menu.MenuQuizActivity

class ListQuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListQuestionBinding
    private lateinit var viewModel: QuizQuestionViewModel
    private lateinit var adapter: QuestionAdapter
    private lateinit var quizSetId: String
    private var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        quizSetId = intent.getStringExtra("quizSetId") ?: run {
            finish()
            return
        }

        Log.d("AddEditQuizActivity", "QuizSetId: $quizSetId")

        val title = intent.getStringExtra("title")
        binding.apply{
            toolbarAction.apply {
                imgActionRight.invisible()
                rootView.backgroundTintList = AppCompatResources.getColorStateList(this@ListQuestionActivity,
                    R.color.primary_800)

                imageView.imageTintList = AppCompatResources.getColorStateList(this@ListQuestionActivity,
                    R.color.white)

                txtTitle.setTextColor(
                    AppCompatResources.getColorStateList(this@ListQuestionActivity,
                        R.color.white))

                txtTitle.text = title
                imageView.setOnClickListener {
                    onBackPressed()
                }
            }
        }

        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        if (::viewModel.isInitialized && ::quizSetId.isInitialized) {
            viewModel.loadQuizzes(quizSetId)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[QuizQuestionViewModel::class.java]
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
                putExtra("student", student)
            }
            Log.d("QuizListActivity", "Sending quizSetId: $quizSetId")
            startActivity(intent)
        }

//        binding.btnSaveQuiz.setOnClickListener {
//            val intent = Intent(this, MenuQuizActivity::class.java).apply {
//                putExtra("student", student)
//            }
//            startActivity(intent)
//            finish()
//        }
    }

    private fun observeViewModel() {
        viewModel.quizzes.observe(this) { quizzes ->
            adapter.submitList(quizzes)
            binding.tvEmptyState.visibility = if (quizzes.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}