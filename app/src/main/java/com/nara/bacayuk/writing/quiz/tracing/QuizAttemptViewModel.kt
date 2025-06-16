package com.nara.bacayuk.writing.quiz.tracing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.domain.usecase.UserUseCase
import com.nara.bacayuk.utils.EMAIL
import com.nara.bacayuk.utils.FULL_NAME_USER
import com.nara.bacayuk.utils.UID
import com.nara.bacayuk.writing.quiz.predict.QuizAnswer
import com.nara.bacayuk.writing.quiz.question.Question
import com.nara.bacayuk.writing.quiz.question.QuizRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class QuizAttemptViewModel(
    private val dataStore: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
    private val userUseCase: UserUseCase,
) : ViewModel() {

    private val repository = QuizRepository()
    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _currentAttempt = MutableLiveData<QuizAttempt>()
    val currentAttempt: LiveData<QuizAttempt> = _currentAttempt

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    fun loadQuestions(userId: String, studentId: String, quizSetId: String) {
        _loading.value = true
        repository.getQuizzesByQuizSetId(
            userId = userId,
            studentId = studentId,
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

    fun getUser(id: String) {
        Log.d("MainViewModel", "getUser: called")
        viewModelScope.launch {
            try {
                userUseCase.getUserFromFirestore(id).collect {
                    Log.d("MainViewModel", "getUser: success")
                    _user.value = it
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "getUser: fail")
                e.printStackTrace()
            }
        }
    }

    fun getUserDataStore(): User? {
        val uid = getUID()
        val email = getEmail()
        val fullName = getFullName()
        return if (uid != null && email != null && fullName != null) {
            User(uid, email, fullName)
        } else {
            null
        }
    }

    fun getEmail(): String? = runBlocking {
        dataStore.getString(EMAIL)
    }

    fun getUID(): String? = runBlocking {
        dataStore.getString(UID)
    }

    fun getFullName(): String? = runBlocking {
        dataStore.getString(FULL_NAME_USER)
    }
}
//
//class QuizAttemptViewModelFactory(
//    private val dataStore: DataStoreRepository,
//    private val reportUseCase: ReportUseCase,
//    private val userUseCase: UserUseCase
//) : ViewModel() {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(QuizAttemptViewModel::class.java)) {
//            return QuizAttemptViewModel(dataStore, reportUseCase, userUseCase) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
//    }
//}
