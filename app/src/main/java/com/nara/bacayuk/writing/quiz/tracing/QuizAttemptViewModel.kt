package com.nara.bacayuk.writing.quiz.tracing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nara.bacayuk.writing.quiz.predict.QuizAnswer
import com.nara.bacayuk.writing.quiz.question.Question
import com.nara.bacayuk.writing.quiz.question.QuizRepository

class QuizAttemptViewModel : ViewModel() {
    private val repository = QuizRepository()
    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _currentAttempt = MutableLiveData<QuizAttempt>()
    val currentAttempt: LiveData<QuizAttempt> = _currentAttempt

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun loadQuestions(quizSetId: String) {
        _loading.value = true
        repository.getQuizzesByQuizSetId(
            quizSetId = quizSetId,
            onSuccess = { questionList ->
                _questions.value = questionList
                initializeAttempt(quizSetId, questionList)
                _loading.value = false
            },
            onFailure = { e ->
                _loading.value = false
            }
        )
    }

    private fun initializeAttempt(quizSetId: String, questions: List<Question>) {
        val attempt = QuizAttempt(
            id = "",
            quizSetId = quizSetId,
            answers = questions.map { QuizAnswer(questionId = it.id) },
            startTime = System.currentTimeMillis()
        )
        _currentAttempt.value = attempt
    }

    fun saveAnswer(index: Int, answer: String) {
        val currentAttempt = _currentAttempt.value ?: return
        val answers = currentAttempt.answers.toMutableList()
        answers[index] = answers[index].copy(userAnswer = answer)
        _currentAttempt.value = currentAttempt.copy(answers = answers)
    }
}
