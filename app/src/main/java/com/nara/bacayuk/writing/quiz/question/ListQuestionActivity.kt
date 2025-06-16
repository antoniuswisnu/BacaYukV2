package com.nara.bacayuk.writing.quiz.question

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityListQuestionBinding
import com.nara.bacayuk.ui.custom_view.ConfirmationDialogRedStyle
import com.nara.bacayuk.utils.invisible

class ListQuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListQuestionBinding
    private lateinit var viewModel: QuizQuestionViewModel
    private lateinit var adapter: QuestionAdapter
    private lateinit var quizSetId: String
    private var student: Student? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        if (student == null) {
            Toast.makeText(this, "Student data is missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        quizSetId = intent.getStringExtra("quizSetId") ?: run {
            finish()
            return
        }

        Log.d("ListQuestionActivity", "UserId: $userId, StudentId: ${student?.uuid}, QuizSetId: $quizSetId")

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
            loadData()
        }
    }

    private fun loadData() {
        if (userId != null && student != null) {
            viewModel.loadQuizzes(userId!!, student!!.uuid, quizSetId)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[QuizQuestionViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = QuestionAdapter(
            onDeleteClick = { quiz ->
                val dialogDelete = ConfirmationDialogRedStyle(
                    this@ListQuestionActivity,
                    icon = R.drawable.ic_baseline_delete_24,
                    title = "Hapus Soal",
                    message = "Apakah Anda yakin ingin menghapus soal ini?",
                    onConfirmClickListener = {
                        if (userId != null && student != null) {
                            viewModel.deleteQuiz(userId!!, student!!.uuid, quiz)
                        }
                    }
                )
                dialogDelete.show()
            },
            student = student!!
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
    }
}