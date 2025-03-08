package com.nara.bacayuk.writing.quiz.question

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuizQuestionViewModel : ViewModel() {
    private val repository = QuizRepository()
    private val _quizzes = MutableLiveData<List<Question>>()
    val quizzes: LiveData<List<Question>> = _quizzes

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var currentQuizSetId: String? = null

    fun loadQuizzes(quizSetId: String) {
        currentQuizSetId = quizSetId
        _loading.value = true
        repository.getQuizzesByQuizSetId(
            quizSetId = quizSetId,
            onSuccess = { quizList ->
                _quizzes.value = quizList
                _loading.value = false
            },
            onFailure = { e ->
                _error.value = e.message
                _loading.value = false
            }
        )
    }

    fun addQuiz(quiz: Question) {
        _loading.value = true
        repository.addQuiz(
            quiz = quiz,
            onSuccess = {
                loadQuizzes(currentQuizSetId ?: "")
                _loading.value = false
            },
            onFailure = { e ->
                _error.value = e.message
                _loading.value = false
            }
        )
    }

    fun updateQuiz(quiz: Question) {
        _loading.value = true
        repository.updateQuiz(
            quiz = quiz,
            onSuccess = {
                loadQuizzes(
                    quizSetId = currentQuizSetId ?: ""
                )
                _loading.value = false
            },
            onFailure = { e ->
                _error.value = e.message
                _loading.value = false
            }
        )
    }

    fun deleteQuiz(quizId: String) {
        _loading.value = true
        repository.deleteQuiz(
            quizId = quizId,
            onSuccess = {
                loadQuizzes(
                    quizSetId = currentQuizSetId ?: ""
                )
                _loading.value = false
            },
            onFailure = { e ->
                _error.value = e.message
                _loading.value = false
            }
        )
    }
}