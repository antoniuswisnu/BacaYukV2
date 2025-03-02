package com.nara.bacayuk.ui.feat_student.list_student

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.AuthUseCase
import com.nara.bacayuk.domain.usecase.StudentUseCase
import com.nara.bacayuk.utils.EMAIL
import com.nara.bacayuk.utils.FULL_NAME_USER
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ListStudentViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val authUseCase: AuthUseCase,
    private val studentUseCase: StudentUseCase,
    ): ViewModel() {

    private val _students = MutableLiveData<Response<List<Student>>>()
    val students: LiveData<Response<List<Student>>> = _students

    private val _isSuccess = MutableLiveData<Response<Boolean>>()
    val isSuccess : MutableLiveData<Response<Boolean>> = _isSuccess

    fun deleteStudentFirestore(idUser: String,idStudent: String) = viewModelScope.launch {
        val result = studentUseCase.deleteStudentFromFirestore(idUser,idStudent)
        if (result){
            _isSuccess.postValue(Response.Success(result))
        } else {
            _isSuccess.postValue(Response.Error(null.toString(), "Galgal Menambahkan"))
        }
    }

    fun logOutUser() = viewModelScope.launch {
        authUseCase.logOut()
        dataStoreRepository.clear()
    }

    fun getAllStudent(id: String){
        viewModelScope.launch {
            try {
                studentUseCase.getAllUserFromFirestore(id).collect {
                    Log.d("ListStudentViewModel", "getUser: success")
                    _students.value = it
                }
            } catch (e: Exception) {
                Log.d("ListStudentViewModel", "getUser: fail")
                e.printStackTrace()
            }
        }
    }


    fun getEmail(): String? = runBlocking {
        dataStoreRepository.getString(EMAIL)
    }

    fun getUID(): String? = runBlocking {
        dataStoreRepository.getString(UID)
    }

    fun getFullName(): String? = runBlocking {
        dataStoreRepository.getString(FULL_NAME_USER)
    }


    fun getUserDataStore(): User? {
        val uid = getUID()
        val email = getEmail()
        val fullName = getFullName()
        return if (uid!= null && email!= null && fullName!= null) {
            User(uid, email, fullName)
        } else {
            null
        }
    }
}