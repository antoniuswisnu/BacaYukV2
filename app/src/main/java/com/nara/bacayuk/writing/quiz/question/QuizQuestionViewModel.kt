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

    private var currentUserId: String? = null
    private var currentStudentId: String? = null
    private var currentQuizSetId: String? = null

    fun loadQuizzes(userId: String, studentId: String, quizSetId: String) {
        currentUserId = userId
        currentStudentId = studentId
        currentQuizSetId = quizSetId

        _loading.value = true
        repository.getQuizzesByQuizSetId(
            userId = userId,
            studentId = studentId,
            quizSetId = quizSetId,
            onSuccess = { quizList ->
                val sortedList = quizList.sortedBy { it.createdAt }
                _quizzes.value = sortedList
                _loading.value = false
            },
            onFailure = { e ->
                _error.value = e.message
                _loading.value = false
            }
        )
    }

    fun addQuiz(userId: String, studentId: String, quiz: Question) {
        _loading.value = true
        repository.addQuiz(
            userId = userId,
            studentId = studentId,
            quiz = quiz,
            onSuccess = {
                _loading.value = false
            },
            onFailure = { e ->
                _error.value = e.message
                _loading.value = false
            }
        )
    }

    fun updateQuiz(userId: String, studentId: String, quiz: Question) {
        _loading.value = true
        repository.updateQuiz(
            userId = userId,
            studentId = studentId,
            quiz = quiz,
            onSuccess = {
                _loading.value = false
            },
            onFailure = { e ->
                _error.value = e.message
                _loading.value = false
            }
        )
    }

    fun deleteQuiz(userId: String, studentId: String, quiz: Question) {
        _loading.value = true
        repository.deleteQuiz(
            userId = userId,
            studentId = studentId,
            quizId = quiz.id,
            quizSetId = quiz.quizSetId,
            onSuccess = {
                _loading.value = false
            },
            onFailure = { e ->
                _error.value = e.message
                _loading.value = false
            }
        )
    }
}