package com.nara.bacayuk.writing.letter.tracing.lowercase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.ReportTulisHuruf
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

class TracingLetterLowercaseViewModel (
    private val dataStore: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
    private val userUseCase: UserUseCase,
) : ViewModel() {

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    private val _reportTulisHurufKecil = MutableLiveData<Response<List<ReportTulisHuruf>>>()
    val reportTulisHurufKecil: LiveData<Response<List<ReportTulisHuruf>>> = _reportTulisHurufKecil

    fun updateReportHurufKecil(
        idUser: String,
        idStudent: String,
        reportTulisHuruf: ReportTulisHuruf
    ) = viewModelScope.launch {
        try {
            reportUseCase.addUpdateReportTulisHuruf(idUser, idStudent, reportTulisHuruf)
        } catch (e: Exception) {
            Log.d("MainViewModel", "login: fail")
            e.printStackTrace()
        }
    }

    fun getReportTulisHurufKecil(idUser: String, idStudent: String) {
        viewModelScope.launch {
            reportUseCase.getAllReportTulisHurufFromFirestore(idUser, idStudent).collect { response ->
                _reportTulisHurufKecil.value = response
            }
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