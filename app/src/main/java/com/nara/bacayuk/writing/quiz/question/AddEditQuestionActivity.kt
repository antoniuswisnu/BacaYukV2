package com.nara.bacayuk.writing.quiz.question

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityAddEditQuestionBinding

class AddEditQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditQuestionBinding
    private lateinit var viewModel: QuizQuestionViewModel
    private var quiz: Question? = null
    private lateinit var quizSetId: String
    private var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
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
        val questionTypes = arrayOf("LETTER", "NUMBER", "WORD")
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
        quizSetId = intent.getStringExtra("quizSetId") ?: run {
            Toast.makeText(this, "Quiz Set ID tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Log.d("AddEditQuizActivity", "QuizSetId: $quizSetId")

        quiz?.let {
            binding.etQuestion.setText(it.question)
            binding.etAnswer.setText(it.correctAnswer)
            binding.spinnerType.setSelection(
                when (it.questionType) {
                    "LETTER" -> 0
                    "NUMBER" -> 1
                    "WORD" -> 2
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

        val newQuiz = Question(
            id = quiz?.id ?: "",
            question = question,
            correctAnswer = answer,
            questionType = questionType,
            quizSetId = quizSetId
        )

        Log.d("AddEditQuizActivity", "Saving question with quizSetId: ${newQuiz.quizSetId}")

        if (quiz == null) {
            viewModel.addQuiz(newQuiz)
        } else {
            viewModel.updateQuiz(newQuiz)
        }

        setResult(RESULT_OK)
        finish()
    }
}