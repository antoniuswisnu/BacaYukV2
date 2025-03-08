package com.nara.bacayuk.ui.feat_baca_kata.quiz

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.ReportKalimat
import com.nara.bacayuk.data.model.ReportKata
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class QuizViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
): ViewModel() {

    private val _reportKatas = MutableLiveData<Response<ReportKata>>()
    val reportKatas: LiveData<Response<ReportKata>> = _reportKatas

    private val _reportKalimat = MutableLiveData<Response<ReportKalimat>>()
    val reportKalimat: LiveData<Response<ReportKalimat>> = _reportKalimat

    fun updateReportKata(
        idStudent: String,
        reportHuruf: ReportKata
    ) = viewModelScope.launch {
        try {
            reportUseCase.updateReportKata(getUID()?: "", idStudent, reportHuruf)
        } catch (e: Exception) {
            Log.d("MainViewModel", "login: fail")
            e.printStackTrace()
        }
    }

    fun updateReportKalimat(
        idStudent: String,
        reportHuruf: ReportKalimat
    ) = viewModelScope.launch {
        try {
            reportUseCase.addUpdateReportKalimat(getUID()?: "", idStudent, reportHuruf)
        } catch (e: Exception) {
            Log.d("MainViewModel", "login: fail")
            e.printStackTrace()
        }
    }

    fun getAllReportKataFromFirestore(idStudent: String){
        viewModelScope.launch {
            try {
                reportUseCase.getAllReportKataFromFirestore(getUID() ?: "-", idStudent).collect {
                    Log.d("ListStudentViewModel", "getUser: success")
                    _reportKatas.value = it
                }
            } catch (e: Exception) {
                Log.d("ListStudentViewModel", "getUser: fail")
                e.printStackTrace()
            }
        }
    }

    fun getAllReportKalimatFromFirestore(idStudent: String){
        viewModelScope.launch {
            try {
                reportUseCase.getAllReportKalimatFromFirestore(getUID() ?: "-", idStudent).collect {
                    Log.d("ListStudentViewModel", "getUser: success")
                    _reportKalimat.value = it
                }
            } catch (e: Exception) {
                Log.d("ListStudentViewModel", "getUser: fail")
                e.printStackTrace()
            }
        }
    }

    fun getUID(): String? = runBlocking {
        dataStoreRepository.getString(UID)
    }

}