package com.nara.bacayuk.data.report

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.nara.bacayuk.data.model.*
import com.nara.bacayuk.utils.MESSAGE_HURUF_SUCCESS
import com.nara.bacayuk.utils.MESSAGE_KATA_SUCCESS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ReportDataSourceImpl: ReportDataSource {

    override suspend fun updateReportHuruf(idUser: String,idStudent: String,reportHuruf: ReportHuruf): Boolean {
        return try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportHuruf").document(reportHuruf.abjadName).set(reportHuruf).await()
            true
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false
        }
    }
    override fun getAllReportFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportHuruf>>> {
        return flow {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val students = mutableListOf<ReportHuruf>()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportHuruf").get().await()
            //get list students
            for (doc in snapshot.documents) {
                doc.toObject(ReportHuruf::class.java)?.let { students.add(it) }
            }
            emit(Response.Success(students))
        }.catch {
            Log.e("getAllUserFromFirestore", "Failed to fetch user data from Firestore.", it)
        }
    }

    override suspend fun createReportHurufDataSets(idUser: String,idStudent: String): String {
        return try {
            val datasetsHuruf = createDataSet()
            var lastStatus = "Menyiapkan data huruf.."
            val firestoreInstance = FirebaseFirestore.getInstance()
            for (item in datasetsHuruf) {
                val documentReference =
                    firestoreInstance.collection("Users").document(idUser)
                        .collection("Students").document(idStudent)
                        .collection("ReportHuruf").document(item.abjadName)

                documentReference.set(item).await()
                if (item.abjadName == "Zz") lastStatus = MESSAGE_HURUF_SUCCESS
            }

            lastStatus // kembalikan nilai boolean true jika operasi berhasil
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            "Gagal menyiapkan data belajar huruf"
        }
    }

    override suspend fun updateReportKata(
        idUser: String,
        idStudent: String,
        reportHuruf: ReportKata
    ): Boolean {
        return try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportKata").document("ReportKata").set(reportHuruf).await()
            true // kembalikan nilai boolean true jika operasi berhasil
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false // kembalikan nilai boolean false jika operasi gagal
        }
    }
    override suspend fun updateBelajarSuku(
        idUser: String,
        idStudent: String,
        reportHuruf: BelajarSuku
    ): Boolean {
        return try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users").document(idUser)
                .collection("Students").document(idStudent)
                .collection("ReportKata").document("ReportKata")
                .collection("BelajarVokal").document(reportHuruf.abjadName).set(reportHuruf).await()
            true // kembalikan nilai boolean true jika operasi berhasil
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false // kembalikan nilai boolean false jika operasi gagal
        }
    }

    override fun getAllReportKataFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<ReportKata>> {
        return flow {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportKata").document("ReportKata").get().await()

            val report = snapshot.toObject(ReportKata::class.java)
            Log.d("getAllReport", report.toString())
            emit(Response.Success(report ?: ReportKata()))
        }.catch {
            Log.e("getAllUserFromFirestore", "Failed to fetch user data from Firestore.", it)
        }
    }

    override fun getAllBelajarVokal(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<BelajarSuku>>> {
        return flow {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val students = mutableListOf<BelajarSuku>()
            val snapshot = firestoreInstance.collection("Users").document(idUser)
                .collection("Students").document(idStudent)
                .collection("ReportKata").document("ReportKata")
                .collection("BelajarVokal").get().await()
            //get list students
            for (doc in snapshot.documents) {
                doc.toObject(BelajarSuku::class.java)?.let { students.add(it) }
            }
            emit(Response.Success(students))
        }.catch {
            Log.e("getAllUserFromFirestore", "Failed to fetch user data from Firestore.", it)
        }
    }

    override suspend fun createReportKataDataSets(
        idUser: String,
        idStudent: String
    ): String {
        return try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val listData = createDataSetKata()
            var lastStatus = "Menyiapkan data kata.."
            listData.forEach{
                lastStatus = "Menyiapkan data kata ${it.abjadName}"
                val documentReference =
                    firestoreInstance.collection("Users").document(idUser)
                        .collection("Students").document(idStudent)
                        .collection("ReportKata").document("ReportKata")
                        .collection("BelajarVokal").document(it.abjadName)
                documentReference.set(it).await()
                if (it.abjadName== "Zz") lastStatus = MESSAGE_KATA_SUCCESS
            }
            return lastStatus
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            "Gagal menyiapkan data belajar kata"
        }
    }

    private fun createDataSet(): List<ReportHuruf>{
        val reportHurufs = mutableListOf<ReportHuruf>()
        val audioHuruf = generateAudioAbjad()
        for (i in 65..90) {
            val huruf = i.toChar().toString() + i.toChar().toLowerCase().toString()
            val reportHuruf = ReportHuruf(abjadName = huruf)
            reportHurufs.add(reportHuruf)
        }
        for (i in audioHuruf.indices){
            reportHurufs[i].audioUrl = audioHuruf[i].urlAudio
        }
        return reportHurufs
    }

    private fun createDataSetKata(): List<BelajarSuku>{
        val reportHurufs = mutableListOf<BelajarSuku>()
        val dataAudio = createDataSetAudioBelajarSuku()
        for (i in 65..90) {
            val huruf = i.toChar().toString() + i.toChar().toLowerCase().toString()
            val reportHuruf = BelajarSuku(abjadName = huruf)
            reportHurufs.add(reportHuruf)
        }
        for (i in dataAudio.indices){
            reportHurufs[i].AudioBelajarSuku = dataAudio[i]
        }

        return reportHurufs
    }

    override suspend fun addUpdateReportKalimat(
        idUser: String,
        idStudent: String,
        reportHuruf: ReportKalimat
    ): Boolean {
        return try {
            var lastStatus = "Menyiapkan data kalimat.."
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportKalimat").document("ReportKalimat").set(reportHuruf).await()
            true // kembalikan nilai boolean true jika operasi berhasil
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false // kembalikan nilai boolean false jika operasi gagal
        }
    }

    override fun getAllReportKalimatFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<ReportKalimat>> {
        return flow {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportKalimat").document("ReportKalimat").get().await()
            val report = snapshot.toObject(ReportKalimat::class.java)
            Log.d("getAllReport", report.toString())
            emit(Response.Success(report ?: ReportKalimat()))
        }.catch {
            Log.e("getAllUserFromFirestore", "Failed to fetch user data from Firestore.", it)
        }
    }

    override suspend fun createReportAngkaDataSets(idUser: String, idStudent: String): String {
        return try {
            val datasetsAngka = createDataSetAngka()
            var lastStatus = "Menyiapkan data angka.."
            val firestoreInstance = FirebaseFirestore.getInstance()
            for (item in datasetsAngka) {
                val documentReference =
                    firestoreInstance.collection("Users").document(idUser)
                        .collection("Students").document(idStudent)
                        .collection("ReportTulisAngka").document(item.tulisAngka)

                documentReference.set(item).await()
                if (item.tulisAngka == "9") lastStatus = MESSAGE_HURUF_SUCCESS
            }
            lastStatus
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            "Gagal menyiapkan data belajar angka"
        }
    }

    private fun createDataSetAngka(): List<ReportTulisAngka>{
        val reportTulisAngkas = mutableListOf<ReportTulisAngka>()
//        val audioHuruf = generateAudioAbjad()
        for (i in 0..9) {
            val angka = i.toChar().toString()
            val reportHuruf = ReportTulisAngka(tulisAngka = angka)
            reportTulisAngkas.add(reportHuruf)
        }
//        for (i in audioHuruf.indices){
//            reportTulisAngkas[i].audioUrl = audioHuruf[i].urlAudio
//        }
        return reportTulisAngkas
    }

    override suspend fun addUpdateReportAngka(
        idUser: String,
        idStudent: String,
        reportTulisAngka: ReportTulisAngka
    ): Boolean {
        return try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportTulisAngka").document("ReportTulisAngka").set(reportTulisAngka).await()
            true
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false
        }
    }

    override fun getAllReportAngkaFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportTulisAngka>>> {
        return flow {
            val firestoreInstance = FirebaseFirestore.getInstance()
//            val students = mutableListOf<ReportTulisAngka>()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportTulisAngka").document("ReportTulisAngka").get().await()
//            for (doc in snapshot.documents) {
//                doc.toObject(ReportTulisAngka::class.java)?.let { students.add(it) }
//            }
            val report = snapshot.toObject(ReportTulisAngka::class.java)
            Log.d("getAllReportTulisAngka", report.toString())
            emit(Response.Success(listOf(report ?: ReportTulisAngka())))
        }.catch {
            Log.e("getAllUserFromFirestore", "Failed to fetch user data from Firestore.", it)
        }
    }
}