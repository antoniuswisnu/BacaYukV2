package com.nara.bacayuk.ui.feat_menu_utama

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nara.bacayuk.data.model.ReportKalimat
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.data.preferences.DataStoreRepository
import com.nara.bacayuk.domain.usecase.AuthUseCase
import com.nara.bacayuk.domain.usecase.ReportUseCase
import com.nara.bacayuk.domain.usecase.StudentUseCase
import com.nara.bacayuk.domain.usecase.UserUseCase
import com.nara.bacayuk.utils.EMAIL
import com.nara.bacayuk.utils.FULL_NAME_USER
import com.nara.bacayuk.utils.MESSAGE_KALIMAT_SUCCESS
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val authUseCase: AuthUseCase,
    private val userUseCase: UserUseCase,
    private val reportUseCase: ReportUseCase,
    private val studentUseCase: StudentUseCase
    ) : ViewModel() {

    private val _statusCreateData = MutableLiveData<ArrayList<String>>()
    val statusCreateData: LiveData<ArrayList<String>> = _statusCreateData

    private val _user = MutableLiveData<Response<User>>()
    val user: LiveData<Response<User>> = _user

    fun createReportHurufDataSets(
        isFirstOpen: Boolean,
        idUser: String,
        idStudent: String,
        student: Student
    ) =
        viewModelScope.launch {
            val user = getUserDataStore()
            val arrayList = arrayListOf("","","","")
            arrayList[0] = "Mempersiapkan data huruf.."
            arrayList[1] = "Mempersiapkan data kata.."
            arrayList[2] = "Mempersiapkan data kalimat.."
            arrayList[3] = "Mempersiapkan data angka.."
            _statusCreateData.value = arrayList
            try {
                if (isFirstOpen) {
                    student.isReadyHurufDataSet = true
                    user?.let {
                        student.isReadyHurufDataSet = true
                        studentUseCase.addUpdateStudentToFirestore(it.uuid ?: "-", student)
                    }

                    val status = reportUseCase.createReportHurufDataSets(idUser, idStudent)
                    val statusKata = reportUseCase.createReportKataDataSets(idUser, idStudent)
                    val statusKalimat = reportUseCase.addUpdateReportKalimat(idUser, idStudent, ReportKalimat())
                    val statusAngka = reportUseCase.createReportAngkaDataSets(idUser, idStudent)

                    val arrayList1 = arrayListOf("","","","")
                    arrayList1[0] = status
                    arrayList1[1] = statusKata
                    arrayList1[2] = if (statusKalimat) MESSAGE_KALIMAT_SUCCESS else "Gagal mempersiapkan data kalimat"
                    arrayList1[3] = statusAngka
                    _statusCreateData.value = arrayList1
                }
            } catch (e: Exception) {
                Log.d("MainViewModel", "login: fail")
                e.printStackTrace()
            }
        }

//    fun createReportAngkaDataSets(
//        isFirstOpen: Boolean,
//        idUser: String,
//        idStudent: String,
//        student: Student
//    ) = viewModelScope.launch {
//        try {
//            if (isFirstOpen) {
//                val status = reportUseCase.createReportAngkaDataSets(idUser, idStudent)
//                val arrayList = arrayListOf(status)
//                _statusCreateData.value = arrayList
//            }
//        } catch (e: Exception) {
//            Log.d("MainViewModel", "login: fail")
//            e.printStackTrace()
//        }
//    }

    fun saveUser(user: User) = viewModelScope.launch {
        userUseCase.addUpdateUserToFirestore(user)
    }

    fun logOutUser() = viewModelScope.launch {
        authUseCase.logOut()
        dataStoreRepository.clear()
    }

    fun getUser(id: String){
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

    fun register(email: String, password: String) = viewModelScope.launch {
        authUseCase.register(email, password)
    }

    fun getEmail(): String? = runBlocking {
        dataStoreRepository.getString(EMAIL)
    }

    fun getUID(): String? = runBlocking {
        dataStoreRepository.getString(UID)
    }

    fun getFullName(): String? = runBlocking {
        dataStoreRepository.getString(FULL_NAME_USER)
    }

    fun getUserDataStore(): User? {
        val uid = getUID()
        val email = getEmail()
        val fullName = getFullName()
        return if (uid!= null && email!= null && fullName!= null) {
            User(uid, email, fullName)
        } else {
            null
        }
    }
}