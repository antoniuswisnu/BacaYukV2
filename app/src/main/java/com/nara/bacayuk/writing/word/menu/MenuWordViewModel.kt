package com.nara.bacayuk.writing.word.menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.ReportTulisKata
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.domain.usecase.UserUseCase
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MenuWordViewModel (
    private val dataStoreRepository: DataStoreRepository,
    private val reportUseCase: ReportUseCase,
     private val userUseCase: UserUseCase
): ViewModel() {

    private val _allWords = MutableLiveData<Response<List<ReportTulisKata>>>()
    val allWords: LiveData<Response<List<ReportTulisKata>>> = _allWords

    private val _easyWords = MutableLiveData<List<ReportTulisKata>>()
    val easyWords: LiveData<List<ReportTulisKata>> = _easyWords

    private val _mediumWords = MutableLiveData<List<ReportTulisKata>>()
    val mediumWords: LiveData<List<ReportTulisKata>> = _mediumWords

    private val _hardWords = MutableLiveData<List<ReportTulisKata>>()
    val hardWords: LiveData<List<ReportTulisKata>> = _hardWords

    private val _addWordStatus = MutableLiveData<Response<String>>()
    val addWordStatus: LiveData<Response<String>> = _addWordStatus

    private val _updateWordStatus = MutableLiveData<Response<Boolean>>()
    val updateWordStatus: LiveData<Response<Boolean>> = _updateWordStatus

    private val _deleteWordStatus = MutableLiveData<Response<Boolean>>()
    val deleteWordStatus: LiveData<Response<Boolean>> = _deleteWordStatus

    fun fetchAllWords(idStudent: String) {
        viewModelScope.launch {
            val uid = getUID()
            if (uid == null) {
                _allWords.value = Response.Error("UID pengguna tidak ditemukan.")
                return@launch
            }
            reportUseCase.getAllReportTulisKata(uid, idStudent)
                .onStart { _allWords.value = Response.Loading }
                .catch { e ->
                    _allWords.value = Response.Error(e.message ?: "Gagal mengambil data kata")
                    Log.e("MenuWordViewModel", "Error fetchAllWords: ${e.message}", e)
                }
                .collect { response ->
                    _allWords.value = response
                    if (response is Response.Success) {
                        categorizeWords(response.data)
                    }
                }
        }
    }

    private fun categorizeWords(words: List<ReportTulisKata>) {
        _easyWords.value = words.filter { it.level == "Mudah" }
        _mediumWords.value = words.filter { it.level == "Sedang" }
        _hardWords.value = words.filter { it.level == "Tinggi" }
    }

    fun addNewWord(idStudent: String, wordText: String) {
        viewModelScope.launch {
            val uid = getUID()
            if (uid == null) {
                _addWordStatus.value = Response.Error("UID pengguna tidak ditemukan.")
                return@launch
            }
            if (wordText.isBlank() || wordText.length > 5) {
                _addWordStatus.value = Response.Error("Kata tidak boleh kosong dan maksimal 5 huruf.")
                return@launch
            }

            val newReport = ReportTulisKata(tulisKata = wordText.uppercase())

            reportUseCase.addReportTulisKata(uid, idStudent, newReport)
                .onStart { _addWordStatus.value = Response.Loading }
                .catch { e ->
                    _addWordStatus.value = Response.Error(e.message ?: "Gagal menambahkan kata")
                    Log.e("MenuWordViewModel", "Error addNewWord: ${e.message}", e)
                }
                .collect { response ->
                    _addWordStatus.value = response
                    if (response is Response.Success) {
                        fetchAllWords(idStudent)
                    }
                }
        }
    }

    fun updateExistingWord(idStudent: String, wordToUpdate: ReportTulisKata) {
        viewModelScope.launch {
            val uid = getUID()
            if (uid == null) {
                _updateWordStatus.value = Response.Error("UID pengguna tidak ditemukan.")
                return@launch
            }
            if (wordToUpdate.tulisKata.isBlank() || wordToUpdate.tulisKata.length > 5) {
                _updateWordStatus.value = Response.Error("Kata tidak boleh kosong dan maksimal 5 huruf.")
                return@launch
            }

            if (wordToUpdate.id.isBlank()) {
                _updateWordStatus.value = Response.Error("ID kata tidak valid untuk pembaruan.")
                return@launch
            }
            val reportToUpdate = wordToUpdate.copy(tulisKata = wordToUpdate.tulisKata.uppercase())

//            reportUseCase.updateReportTulisKata(uid, idStudent, reportToUpdate)
//                .onStart { _updateWordStatus.value = Response.Loading }
//                .catch { e ->
//                    _updateWordStatus.value = Response.Error(e.message ?: "Gagal memperbarui kata")
//                    Log.e("MenuWordViewModel", "Error updateExistingWord: ${e.message}", e)
//                }
//                .collect { response ->
//                    _updateWordStatus.value = response
//                    if (response is Response.Success && response.data) {
//                        fetchAllWords(idStudent)
//                    }
//                }
//

        }
    }

    fun deleteSpecificWord(idStudent: String, wordId: String) {
        viewModelScope.launch {
            val uid = getUID()
            if (uid == null) {
                _deleteWordStatus.value = Response.Error("UID pengguna tidak ditemukan.")
                return@launch
            }
            if (wordId.isBlank()) {
                _deleteWordStatus.value = Response.Error("ID kata tidak valid untuk penghapusan.")
                return@launch
            }

            reportUseCase.deleteReportTulisKata(uid, idStudent, wordId)
                .onStart { _deleteWordStatus.value = Response.Loading }
                .catch { e ->
                    _deleteWordStatus.value = Response.Error(e.message ?: "Gagal menghapus kata")
                    Log.e("MenuWordViewModel", "Error deleteSpecificWord: ${e.message}", e)
                }
                .collect { response ->
                    _deleteWordStatus.value = response
                    if (response is Response.Success && response.data) {
                        fetchAllWords(idStudent) // Muat ulang daftar kata
                    }
                }
        }
    }


    fun getUID(): String? = runBlocking {
        dataStoreRepository.getString(UID)
    }
}
