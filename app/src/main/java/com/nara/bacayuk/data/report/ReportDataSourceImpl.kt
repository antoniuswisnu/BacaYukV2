package com.nara.bacayuk.data.report

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.nara.bacayuk.data.model.*
import com.nara.bacayuk.utils.MESSAGE_HURUF_SUCCESS
import com.nara.bacayuk.utils.MESSAGE_KATA_SUCCESS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ReportDataSourceImpl: ReportDataSource {

    // Baca Huruf
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
            for (doc in snapshot.documents) {
                doc.toObject(ReportHuruf::class.java)?.let { students.add(it) }
            }
            emit(Response.Success(students))
        }.catch {
            Log.e("getAllUserFromFirestore", "Failed to fetch user data from Firestore.", it)
        }
    }

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

    // Baca Kata
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

    // Baca Kalimat
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

    // Tulis Angka
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
//        val audioAngka = generateAudioAngka()
        for (i in 0..9) {
            val angka = i.toString()
            val reportTulisAngka = ReportTulisAngka(
                tulisAngka = angka
            )
            Log.d("createDataSetAngka", reportTulisAngka.toString())
            Log.d("createDataSetAngka", "i : $i")
            Log.d("createDataSetAngka", "angka : $angka")
            reportTulisAngkas.add(reportTulisAngka)
        }
//        for (i in audioAngka.indices){
//            reportTulisAngkas[i].audioUrl = audioAngka[i].urlAudio
//        }
        return reportTulisAngkas
    }

    override fun getAllReportAngkaFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportTulisAngka>>> {
        return flow {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val students = mutableListOf<ReportTulisAngka>()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportTulisAngka").get().await()
            for (doc in snapshot.documents) {
                doc.toObject(ReportTulisAngka::class.java)?.let { students.add(it) }
            }
            emit(Response.Success(students))
        }.catch {
            Log.e("getAllUserFromFirestore", "Failed to fetch user data from Firestore.", it)
        }
    }

    override suspend fun addUpdateReportAngka(
        idUser: String,
        idStudent: String,
        reportTulisAngka: ReportTulisAngka
    ): Boolean {
        return try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val reportRef = firestoreInstance.collection("Users")
                .document(idUser)
                .collection("Students")
                .document(idStudent)
                .collection("ReportTulisAngka")

            if (reportTulisAngka.tulisAngka.isNotEmpty()) {
                reportRef.document(reportTulisAngka.tulisAngka)
                    .set(reportTulisAngka, SetOptions.merge())
                    .await()
            } else {
                reportRef.document()
                    .set(reportTulisAngka)
                    .await()
            }
            true
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error updating ReportTulisAngka: ${e.message}")
            false
        }
    }

    // Tulis Huruf
    override suspend fun createReportTulisHurufDataSets(idUser: String, idStudent: String): String {
        return try {
            val datasetTulisHuruf = createDatasetTulisHuruf()
            var lastStatus = "Menyiapkan data tulis huruf.."
            val firestoreInstance = FirebaseFirestore.getInstance()
            for (item in datasetTulisHuruf) {
                val documentReference =
                    firestoreInstance.collection("Users").document(idUser)
                        .collection("Students").document(idStudent)
                        .collection("ReportTulisHuruf").document(item.tulisHuruf)

                documentReference.set(item).await()
                if (item.tulisHuruf == "Z") lastStatus = MESSAGE_HURUF_SUCCESS
            }
            lastStatus
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            "Gagal menyiapkan data belajar tulis huruf"
        }
    }

    private fun createDatasetTulisHuruf(): List<ReportTulisHuruf>{
        val reportTulisHurufs = mutableListOf<ReportTulisHuruf>()
//        val audioHuruf = generateAudioAbjad()
        for (i in 65..90) {
            val huruf = i.toChar().toString()
            val reportTulisHuruf = ReportTulisHuruf(
                tulisHuruf = huruf
            )
            reportTulisHurufs.add(reportTulisHuruf)
        }
//        for (i in audioHuruf.indices){
//            reportTulisHurufs[i].audioUrl = audioHuruf[i].urlAudio
//        }
        return reportTulisHurufs
    }

    override fun getAllReportTulisHurufFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportTulisHuruf>>> {
        return flow {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val students = mutableListOf<ReportTulisHuruf>()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportTulisHuruf").get().await()
            for (doc in snapshot.documents) {
                doc.toObject(ReportTulisHuruf::class.java)?.let { students.add(it) }
            }
            emit(Response.Success(students))
        }.catch {
            Log.e("getAllUserFromFirestore", "Failed to fetch user data from Firestore.", it)
        }
    }

    override suspend fun updateReportTulisHuruf(
        idUser: String,
        idStudent: String,
        reportTulisHuruf: ReportTulisHuruf
    ): Boolean {
        return try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportTulisHuruf").document(reportTulisHuruf.tulisHuruf).set(reportTulisHuruf).await()
            true
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false
        }
    }

    // Tulis Kata
    override suspend fun createReportTulisKataDataSets(idUser: String, idStudent: String): String {
        return try {
            val datasetTulisKata = createDatasetTulisKata()
            var lastStatus = "Menyiapkan data tulis kata.."
            val firestoreInstance = FirebaseFirestore.getInstance()
            for (item in datasetTulisKata) {
                val documentReference =
                    firestoreInstance.collection("Users").document(idUser)
                        .collection("Students").document(idStudent)
                        .collection("ReportTulisKata").document(item.tulisKata)

                documentReference.set(item).await()
                if (item.tulisKata == "Z") lastStatus = MESSAGE_KATA_SUCCESS
            }
            lastStatus
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            "Gagal menyiapkan data belajar tulis kata"
        }
    }

    private fun createDatasetTulisKata(): List<ReportTulisKata>{
        val reportTulisKatas = mutableListOf<ReportTulisKata>()
        val listKata = listOf(
            "Buku",
            "Tulis",
            "Baca",
            "Huruf",
            "Saya",
            "Ayah",
            "Ibu",
            "Bisa",
            "Pintar",
            "Baik"
        )
        for (i in listKata) {
            val reportTulisKata = ReportTulisKata(
                tulisKata = i
            )
            reportTulisKatas.add(reportTulisKata)
        }
        return reportTulisKatas
    }

    override fun getAllReportTulisKataFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportTulisKata>>> {
        return flow {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val students = mutableListOf<ReportTulisKata>()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportTulisKata").get().await()
            for (doc in snapshot.documents) {
                doc.toObject(ReportTulisKata::class.java)?.let { students.add(it) }
            }
            emit(Response.Success(students))
        }.catch {
            Log.e("getAllUserFromFirestore", "Failed to fetch user data from Firestore.", it)
        }
    }

    override suspend fun addUpdateReportTulisKata(
        idUser: String,
        idStudent: String,
        reportTulisKata: ReportTulisKata
    ): Boolean {
        return try {
            val firestoreInstance = FirebaseFirestore.getInstance()
            val snapshot = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportTulisKata").document(reportTulisKata.tulisKata).set(reportTulisKata).await()
            true
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false
        }
    }

    // Kuis Tulis

//    override suspend fun addUpdateReportKuisTulis(
//        idUser: String,
//        idStudent: String,
//        reportKuisTulis: ReportTulisQuiz
//    ): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun getAllReportKuisTulisFromFirestore(
//        idUser: String,
//        idStudent: String
//    ): Flow<Response<List<ReportTulisQuiz>>> {
//        TODO("Not yet implemented")
//    }
}