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

            lastStatus
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
            true
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false
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
            true
        } catch (e: Exception) {
            Log.e("UserDataSourceImpl", "Error adding or updating user to Firestore.", e)
            false
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
    private val firestoreInstance = FirebaseFirestore.getInstance()

    private fun determineWordLevel(word: String): String {
        return when (word.length) {
            in 1..3 -> "Mudah"
            4 -> "Sedang"
            5 -> "Tinggi"
            else -> {
                if (word.length > 5) "Tinggi"
                else "Tidak Diketahui"
            }
        }
    }

    override fun getAllReportTulisKata(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportTulisKata>>> {
        return flow {
            emit(Response.Loading)
            val reportCollection = firestoreInstance.collection("Users")
                .document(idUser).collection("Students").document(idStudent)
                .collection("ReportTulisKata")

            val snapshot = reportCollection.get().await()
            val reports = snapshot.documents.mapNotNull { document ->
                document.toObject(ReportTulisKata::class.java)?.apply {
                    id = document.id
                    if (level.isEmpty()) {
                        level = determineWordLevel(tulisKata)
                    }
                }
            }
            emit(Response.Success(reports))
        }.catch { e ->
            Log.e("ReportDataSourceImpl", "Error getAllReportTulisKata: ${e.message}", e)
            emit(Response.Error(e.message ?: "Gagal mengambil data ReportTulisKata"))
        }
    }

    override suspend fun addReportTulisKata(
        idUser: String,
        idStudent: String,
        reportTulisKata: ReportTulisKata
    ): Flow<Response<String>> {
        return flow {
            emit(Response.Loading)
            try {
                if (reportTulisKata.tulisKata.isBlank() || reportTulisKata.tulisKata.length > 5) {
                    emit(Response.Error("Kata tidak valid atau lebih dari 5 huruf."))
                    return@flow
                }

                val collectionRef = firestoreInstance.collection("Users")
                    .document(idUser).collection("Students").document(idStudent)
                    .collection("ReportTulisKata")

                val querySnapshot = collectionRef.whereEqualTo("tulisKata", reportTulisKata.tulisKata).get().await()
                if (!querySnapshot.isEmpty) {
                    emit(Response.Error("Kata '${reportTulisKata.tulisKata}' sudah ada."))
                    return@flow
                }

                val newWord = reportTulisKata.copy(
                    level = determineWordLevel(reportTulisKata.tulisKata),
                    id = ""
                )

                val documentReference = collectionRef.add(newWord).await()
                emit(Response.Success(documentReference.id))
            } catch (e: Exception) {
                Log.e("ReportDataSourceImpl", "Error addReportTulisKata: ${e.message}", e)
                emit(Response.Error(e.message ?: "Gagal menambahkan kata"))
            }
        }
    }


    override suspend fun updateReportTulisKata(
        idUser: String,
        idStudent: String,
        reportTulisKata: ReportTulisKata
    ): Flow<Response<Boolean>> {
        return flow {
            emit(Response.Loading)
            if (reportTulisKata.id.isBlank()) {
                emit(Response.Error("ID kata tidak valid untuk pembaruan."))
                return@flow
            }
            if (reportTulisKata.tulisKata.isBlank() || reportTulisKata.tulisKata.length > 5) {
                emit(Response.Error("Kata tidak valid atau lebih dari 5 huruf untuk pembaruan."))
                return@flow
            }

            try {
                val documentRef = firestoreInstance.collection("Users")
                    .document(idUser).collection("Students").document(idStudent)
                    .collection("ReportTulisKata").document(reportTulisKata.id)

                val querySnapshot = firestoreInstance.collection("Users")
                    .document(idUser).collection("Students").document(idStudent)
                    .collection("ReportTulisKata")
                    .whereEqualTo("tulisKata", reportTulisKata.tulisKata)
                    .get().await()

                if (!querySnapshot.isEmpty) {
                    val existingDoc = querySnapshot.documents.first()
                    if (existingDoc.id != reportTulisKata.id) {
                        emit(Response.Error("Kata '${reportTulisKata.tulisKata}' sudah ada (digunakan oleh kata lain)."))
                        return@flow
                    }
                }


                val updatedWord = reportTulisKata.copy(
                    level = determineWordLevel(reportTulisKata.tulisKata)
                )
                documentRef.set(updatedWord, SetOptions.merge()).await()
                emit(Response.Success(true))
            } catch (e: Exception) {
                Log.e("ReportDataSourceImpl", "Error updateReportTulisKata: ${e.message}", e)
                emit(Response.Error(e.message ?: "Gagal memperbarui kata"))
            }
        }
    }

    override suspend fun deleteReportTulisKata(
        idUser: String,
        idStudent: String,
        wordId: String
    ): Flow<Response<Boolean>> {
        return flow {
            emit(Response.Loading)
            if (wordId.isBlank()) {
                emit(Response.Error("ID kata tidak valid untuk penghapusan."))
                return@flow
            }
            try {
                firestoreInstance.collection("Users")
                    .document(idUser).collection("Students").document(idStudent)
                    .collection("ReportTulisKata").document(wordId).delete().await()
                emit(Response.Success(true))
            } catch (e: Exception) {
                Log.e("ReportDataSourceImpl", "Error deleteReportTulisKata: ${e.message}", e)
                emit(Response.Error(e.message ?: "Gagal menghapus kata"))
            }
        }
    }
}