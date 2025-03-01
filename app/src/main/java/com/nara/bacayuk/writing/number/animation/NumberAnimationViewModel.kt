package com.nara.bacayuk.writing.number.animation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.ReportTulisAngka
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.domain.usecase.UserUseCase
import com.nara.bacayuk.utils.EMAIL
import com.nara.bacayuk.utils.FULL_NAME_USER
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NumberAnimationViewModel (
    private val dataStore: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    fun updateReportAngka(
        idUser: String,
        idStudent: String,
        reportTulisAngka: ReportTulisAngka
    ) = viewModelScope.launch {
        try {
            reportUseCase.addUpdateReportTulisAngka(idUser, idStudent, reportTulisAngka)
        } catch (e: Exception) {
            Log.d("MainViewModel", "login: fail")
            e.printStackTrace()
        }
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

    private fun getEmail(): String? = runBlocking {
        dataStore.getString(EMAIL)
    }

    fun getUID(): String? = runBlocking {
        dataStore.getString(UID)
    }

    private fun getFullName(): String? = runBlocking {
        dataStore.getString(FULL_NAME_USER)
    }

}