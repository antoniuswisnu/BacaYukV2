package com.nara.bacayuk.ui.feat_baca_huruf.menu_baca_huruf

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.*
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.domain.usecase.UserUseCase
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MenuBacaHurufViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {

    private val _reports = MutableLiveData<Response<List<ReportHuruf>>>()
    val reports: LiveData<Response<List<ReportHuruf>>> = _reports

    private val _reportKatas = MutableLiveData<Response<ReportKata>>()
    val reportKatas: LiveData<Response<ReportKata>> = _reportKatas

    private val _vokals = MutableLiveData<Response<List<BelajarSuku>>>()
    val vokals: LiveData<Response<List<BelajarSuku>>> = _vokals

    fun updateBelajarSuku(
        idStudent: String,
        reportHuruf: BelajarSuku
    ) = viewModelScope.launch {
        try {
            reportUseCase.updateBelajarSuku(getUID()?: "", idStudent, reportHuruf)
        } catch (e: Exception) {
            Log.d("MainViewModel", "login: fail")
            e.printStackTrace()
        }
    }

    fun getAllReports(idStudent: String){
        viewModelScope.launch {
            try {
                reportUseCase.getAllReportFromFirestore(getUID() ?: "-", idStudent).collect { response ->
                    if (response is Response.Success) {
                        response.data.forEach { report ->
                            Log.d("MenuNumberViewModel", "Fetched Report MenuBacaHuruf: $report")
                        }
                    }
                    _reports.value = response
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    fun getAllBelajarVokal(idStudent: String){
        viewModelScope.launch {
            try {
                reportUseCase.getAllBelajarVokal(getUID() ?: "-", idStudent).collect {
                    Log.d("ListStudentViewModel", "getUser: success")
                    _vokals.value = it
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