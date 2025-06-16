package com.nara.bacayuk.writing.quiz.question

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityAddEditQuestionBinding
import com.nara.bacayuk.utils.invisible

class AddEditQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditQuestionBinding
    private lateinit var viewModel: QuizQuestionViewModel
    private var quiz: Question? = null
    private lateinit var quizSetId: String
    private var student: Student? = null
    private var userId: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = FirebaseAuth.getInstance().currentUser?.uid

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        quiz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("quiz", Question::class.java)
        } else {
            intent.getParcelableExtra("quiz") as Question?
        }

        quizSetId = intent.getStringExtra("quizSetId") ?: ""

        binding.apply{
            toolbarAction.apply {
                imgActionRight.invisible()
                rootView.backgroundTintList = AppCompatResources.getColorStateList(this@AddEditQuestionActivity,
                    R.color.white)

                imageView.imageTintList = AppCompatResources.getColorStateList(this@AddEditQuestionActivity,
                    R.color.primary_800)

                txtTitle.setTextColor(
                    AppCompatResources.getColorStateList(this@AddEditQuestionActivity,
                        R.color.primary_800))

                if (quiz == null) {
                    btnSave.text = "Tambah Soal"
                    txtTitle.text = "Tambah Soal"
                } else {
                    btnSave.text = "Update Soal"
                    txtTitle.text = "Update Soal"
                }
                imageView.setOnClickListener {
                    finish()
                }
            }
        }


        setupViewModel()
        setupSpinner()
        setupClickListeners()
        loadQuizData()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[QuizQuestionViewModel::class.java]
    }

    private fun setupSpinner() {
        val questionTypes = arrayOf("Huruf", "Angka", "Kata")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, questionTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveQuiz()
        }
    }

    private fun loadQuizData() {
        quiz?.let {
            binding.etQuestion.setText(it.question)
            binding.etAnswer.setText(it.correctAnswer)
            binding.spinnerType.setSelection(
                when (it.questionType) {
                    "Huruf" -> 0
                    "Angka" -> 1
                    "Kata" -> 2
                    else -> 0
                }
            )
        }
    }

    private fun saveQuiz() {
        val question = binding.etQuestion.text.toString()
        val answer = binding.etAnswer.text.toString()
        val questionType = binding.spinnerType.selectedItem.toString()

        if (question.isBlank() || answer.isBlank()) {
            Toast.makeText(this, "Pertanyaan dan jawaban harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == null || student == null) {
            Toast.makeText(this, "Data pengguna atau siswa tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        val newQuiz = Question(
            id = quiz?.id ?: "",
            question = question,
            correctAnswer = answer,
            questionType = questionType,
            quizSetId = quizSetId
        )

        Log.d("AddEditQuestionActivity", "Saving question for UserId: $userId, StudentId: ${student!!.uuid}")

        if (quiz == null) {
            viewModel.addQuiz(userId!!, student!!.uuid, newQuiz)
        } else {
            viewModel.updateQuiz(userId!!, student!!.uuid, newQuiz)
        }

        finish()
    }
}