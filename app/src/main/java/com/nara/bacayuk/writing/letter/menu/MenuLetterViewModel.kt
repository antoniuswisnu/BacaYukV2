package com.nara.bacayuk.writing.letter.menu

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.ReportTulisHuruf
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.domain.usecase.UserUseCase
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MenuLetterViewModel (
    private val dataStoreRepository: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {

    private val _reportsLetter = MutableLiveData<Response<List<ReportTulisHuruf>>>()
    val reportsLetter: MutableLiveData<Response<List<ReportTulisHuruf>>> = _reportsLetter

    fun updateReportTulisHuruf(
        idStudent: String,
        reportTulisHuruf: ReportTulisHuruf
    ) = viewModelScope.launch {
        try {
            reportUseCase.addUpdateReportTulisHuruf(getUID()?: "", idStudent, reportTulisHuruf)
        } catch (e: Exception) {
            Log.d("MainViewModel", "login: fail")
            e.printStackTrace()
        }
    }

    fun getAllReportTulisHurufFromFirestore(idStudent: String){
        viewModelScope.launch {
            try {
                reportUseCase.getAllReportTulisHurufFromFirestore(getUID() ?: "-", idStudent).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data.forEach { report ->
                                Log.d("MenuLetterViewModel", "Report data: $report")
                            }
                            _reportsLetter.value = response
                        }
                        is Response.Error -> {
                            _reportsLetter.value = response
                        }
                        is Response.Loading -> {
                            _reportsLetter.value = response
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
