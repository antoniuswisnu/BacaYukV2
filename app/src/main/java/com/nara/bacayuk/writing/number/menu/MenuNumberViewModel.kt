package com.nara.bacayuk.writing.number.menu

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.ReportTulisAngka
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.domain.usecase.UserUseCase
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MenuNumberViewModel (
    private val dataStoreRepository: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {

    private val _reportsNumber = MutableLiveData<Response<List<ReportTulisAngka>>>()
    val reportsNumber: MutableLiveData<Response<List<ReportTulisAngka>>> = _reportsNumber

    fun updateReportTulisAngka(
        idStudent: String,
        reportTulisAngka: ReportTulisAngka
    ) = viewModelScope.launch {
        try {
            reportUseCase.addUpdateReportTulisAngka(getUID()?: "", idStudent, reportTulisAngka)
        } catch (e: Exception) {
            Log.d("MainViewModel", "login: fail")
            e.printStackTrace()
        }
    }

    fun getAllReportTulisAngkaFromFirestore(idStudent: String){
        viewModelScope.launch {
            try {
                reportUseCase.getAllReportTulisAngkaFromFirestore(getUID() ?: "-", idStudent).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data.forEach { report ->
                                Log.d("MenuNumberViewModel", "Report data: $report")
                            }
                            _reportsNumber.value = response
                        }
                        is Response.Error -> {
                            _reportsNumber.value = response
                        }
                        is Response.Loading -> {
                            _reportsNumber.value = response
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getUID(): String? = runBlocking {
        dataStoreRepository.getString(UID)
    }
}