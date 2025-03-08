package com.nara.bacayuk.writing.word.menu

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.ReportTulisKata
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.domain.usecase.UserUseCase
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MenuWordViewModel (
    private val dataStoreRepository: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {

    private val _reportsWord = MutableLiveData<Response<List<ReportTulisKata>>>()
    val reportsWord: MutableLiveData<Response<List<ReportTulisKata>>> = _reportsWord

    fun updateReportTulisKata(
        idStudent: String,
        reportTulisKata: ReportTulisKata
    ) = viewModelScope.launch {
        try {
            reportUseCase.addUpdateReportTulisKata(getUID()?: "", idStudent, reportTulisKata)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllReportTulisKataFromFirestore(idStudent: String){
        viewModelScope.launch {
            try {
                reportUseCase.getAllReportTulisKataFromFirestore(getUID() ?: "-", idStudent).collect { response ->
                    when (response) {
                        is Response.Success -> {
                            response.data.forEach { report ->
                                 Log.d("MenuWordViewModel", "Report data: $report")
                            }
                            _reportsWord.value = response
                        }
                        is Response.Error -> {
                            _reportsWord.value = response
                        }
                        is Response.Loading -> {
                            _reportsWord.value = response
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