package com.nara.bacayuk.ui.feat_riwayat.huruf

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

class RiwayatViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
    private val userUseCase: UserUseCase
): ViewModel() {

    private val _reports = MutableLiveData<Response<List<ReportHuruf>>>()
    val reports: LiveData<Response<List<ReportHuruf>>> = _reports

    private val _reportKata = MutableLiveData<Response<ReportKata>>()
    val reportKata: LiveData<Response<ReportKata>> = _reportKata

    private val _reportKalimat = MutableLiveData<Response<ReportKalimat>>()
    val reportKalimat: LiveData<Response<ReportKalimat>> = _reportKalimat

    private val _vokals = MutableLiveData<Response<List<BelajarSuku>>>()
    val vokals: LiveData<Response<List<BelajarSuku>>> = _vokals

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

    fun getAllReportKataFromFirestore(idStudent: String){
        viewModelScope.launch {
            try {
                reportUseCase.getAllReportKataFromFirestore(getUID() ?: "-", idStudent).collect {
                    Log.d("ListStudentViewModel", "getUser: success")
                    _reportKata.value = it
                }
            } catch (e: Exception) {
                Log.d("ListStudentViewModel", "getUser: fail")
                e.printStackTrace()
            }
        }
    }

    fun getAllReports(idStudent: String){
        viewModelScope.launch {
            try {
                reportUseCase.getAllReportFromFirestore(getUID() ?: "-", idStudent).collect {
                    Log.d("ListStudentViewModel", "getUser: success")
                    _reports.value = it
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