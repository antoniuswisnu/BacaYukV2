package com.nara.bacayuk.writing.word.tracing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.ReportTulisKata
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.domain.usecase.UserUseCase
import com.nara.bacayuk.utils.EMAIL
import com.nara.bacayuk.utils.FULL_NAME_USER
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TracingWordViewModel (
    private val dataStore: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
    private val userUseCase: UserUseCase,
) : ViewModel() {

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    private val _reportTulisKata = MutableLiveData<Response<List<ReportTulisKata>>>()
    val reportTulisKata: LiveData<Response<List<ReportTulisKata>>> = _reportTulisKata

    private val _activeWordReport = MutableLiveData<Response<ReportTulisKata?>>()
    val activeWordReport: LiveData<Response<ReportTulisKata?>> = _activeWordReport

    // Untuk status pembaruan progres kata
    private val _updateProgressStatus = MutableLiveData<Response<Boolean>>()
    val updateProgressStatus: LiveData<Response<Boolean>> = _updateProgressStatus

    fun updateReportKata(
        idUser: String,
        idStudent: String,
        reportTulisKata: ReportTulisKata
    ) = viewModelScope.launch {
        try {
            reportUseCase.updateReportTulisKata(idUser, idStudent, reportTulisKata)
        } catch (e: Exception) {
            Log.d("MainViewModel", "login: fail")
            e.printStackTrace()
        }
    }

    fun getReportTulisKata(idUser: String, idStudent: String) {
        viewModelScope.launch {
            reportUseCase.getAllReportTulisKata(idUser, idStudent).collect { response ->
                _reportTulisKata.value = response
            }
        }
    }

    fun fetchSpecificWordReport(idUser: String, idStudent: String, wordText: String) {
        viewModelScope.launch {
            reportUseCase.getAllReportTulisKata(idUser, idStudent)
                .onStart { _activeWordReport.value = Response.Loading }
                .catch { e ->
                    _activeWordReport.value = Response.Error(e.message ?: "Gagal mengambil data kata spesifik")
                    Log.e("TracingWordVM", "Error fetchSpecificWordReport: ${e.message}", e)
                }
                .collect { response ->
                    when (response) {
                        is Response.Success -> {
                            val specificWord = response.data.firstOrNull { it.tulisKata.equals(wordText, ignoreCase = true) }
                            if (specificWord != null) {
                                _activeWordReport.value = Response.Success(specificWord)
                            } else {
                                _activeWordReport.value = Response.Error("Kata '$wordText' tidak ditemukan.")
                                Log.w("TracingWordVM", "Kata '$wordText' tidak ditemukan untuk student $idStudent")
                            }
                        }
                        is Response.Error -> {
                            _activeWordReport.value = response.message?.let { Response.Error(it) }
                        }
                        is Response.Loading -> {
                            _activeWordReport.value = Response.Loading
                        }
                    }
                }
        }
    }

    /**
     * Memperbarui progres (materiTulisKata, latihanTulisKata) untuk ReportTulisKata yang diberikan.
     * Objek reportTulisKata yang diterima harus sudah memiliki 'id' yang benar dari Firestore
     * dan field boolean yang sudah diubah.
     */
    fun updateWordProgress(idUser: String, idStudent: String, reportToUpdate: ReportTulisKata) {
        viewModelScope.launch {
            if (reportToUpdate.id.isBlank()) {
                _updateProgressStatus.value = Response.Error("ID Kata tidak valid untuk pembaruan progres.")
                Log.e("TracingWordVM", "Attempted to update word progress with blank ID for word: ${reportToUpdate.tulisKata}")
                return@launch
            }

            // Memastikan field yang akan diupdate (materi & latihan) sudah benar-benar true
            // dan `tulisKata` juga sesuai (meskipun ID adalah kunci utama).
            // Level akan di-handle oleh DataSource/Repository jika ada perubahan pada `tulisKata`.
            val finalReportToUpdate = reportToUpdate.copy(
                materiTulisKata = true, // Pastikan ini memang true
                latihanTulisKata = true // Pastikan ini memang true
            )

            reportUseCase.updateReportTulisKata(idUser, idStudent, finalReportToUpdate)
                .onStart { _updateProgressStatus.value = Response.Loading }
                .catch { e ->
                    _updateProgressStatus.value = Response.Error(e.message ?: "Gagal memperbarui progres kata")
                    Log.e("TracingWordVM", "Error updateWordProgress: ${e.message}", e)
                }
                .collect { response ->
                    _updateProgressStatus.value = response
                    if (response is Response.Success && response.data) {
                        Log.i("TracingWordVM", "Progres kata '${finalReportToUpdate.tulisKata}' berhasil diperbarui.")
                        // Jika perlu, bisa memuat ulang activeWordReport untuk mendapatkan data terbaru
                        // fetchSpecificWordReport(idUser, idStudent, finalReportToUpdate.tulisKata)
                    } else if (response is Response.Error) {
                        Log.e("TracingWordVM", "Gagal memperbarui progres kata di Firestore: ${response.message}")
                    }
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