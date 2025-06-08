package com.nara.bacayuk.domain.repository

import com.nara.bacayuk.data.model.*
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    // Baca Huruf
    suspend fun createReportHurufDataSets(idUser: String,idStudent: String): String
    suspend fun updateReportHuruf(idUser: String,idStudent: String,reportHuruf: ReportHuruf): Boolean
    fun getAllReportFromFirestore(idUser: String, idStudent: String): Flow<Response<List<ReportHuruf>>>

    // Baca Kata
    suspend fun createReportKataDataSets(idUser: String,idStudent: String): String
    suspend fun updateReportKata(idUser: String,idStudent: String,reportHuruf: ReportKata): Boolean
    fun getAllReportKataFromFirestore(idUser: String, idStudent: String): Flow<Response<ReportKata>>
    fun getAllBelajarVokal(idUser: String, idStudent: String): Flow<Response<List<BelajarSuku>>>
    suspend fun updateBelajarSuku(idUser: String,idStudent: String,reportHuruf: BelajarSuku): Boolean

    // Baca Kalimat
    suspend fun addUpdateReportKalimat(idUser: String,idStudent: String,reportHuruf: ReportKalimat): Boolean
    fun getAllReportKalimatFromFirestore(idUser: String, idStudent: String): Flow<Response<ReportKalimat>>

    // Tulis Angka
    suspend fun createReportAngkaDataSets(idUser: String,idStudent: String): String
    suspend fun getAllReportTulisAngkaFromFirestore(idUser: String, idStudent: String): Flow<Response<List<ReportTulisAngka>>>
    suspend fun addUpdateReportTulisAngka(idUser: String, idStudent: String, reportTulisAngka: ReportTulisAngka): Boolean

    // Tulis Huruf
    suspend fun createReportTulisHurufDataSets(idUser: String,idStudent: String): String
    suspend fun getAllReportTulisHurufFromFirestore(idUser: String, idStudent: String): Flow<Response<List<ReportTulisHuruf>>>
    suspend fun addUpdateReportTulisHuruf(idUser: String, idStudent: String, reportTulisHuruf: ReportTulisHuruf): Boolean

    // Tulis Kata
    suspend fun addReportTulisKata(idUser: String, idStudent: String, reportTulisKata: ReportTulisKata): Flow<Response<String>>
    suspend fun updateReportTulisKata(idUser: String, idStudent: String, reportTulisKata: ReportTulisKata): Flow<Response<Boolean>>
    suspend fun deleteReportTulisKata(idUser: String, idStudent: String, wordId: String): Flow<Response<Boolean>>
    fun getAllReportTulisKata(idUser: String, idStudent: String): Flow<Response<List<ReportTulisKata>>>
}