package com.nara.bacayuk.data.report

import com.nara.bacayuk.data.model.*
import kotlinx.coroutines.flow.Flow

interface ReportDataSource {
    //huruf
    suspend fun createReportHurufDataSets(idUser: String,idStudent: String): String
    suspend fun updateReportHuruf(idUser: String,idStudent: String,reportHuruf: ReportHuruf): Boolean
    fun getAllReportFromFirestore(idUser: String, idStudent: String): Flow<Response<List<ReportHuruf>>>

    //kata
    suspend fun createReportKataDataSets(idUser: String,idStudent: String): String
    suspend fun updateReportKata(idUser: String,idStudent: String,reportHuruf: ReportKata): Boolean
    suspend fun updateBelajarSuku(idUser: String,idStudent: String,reportHuruf: BelajarSuku): Boolean
    fun getAllReportKataFromFirestore(idUser: String, idStudent: String): Flow<Response<ReportKata>>
    fun getAllBelajarVokal(idUser: String, idStudent: String): Flow<Response<List<BelajarSuku>>>

    //kalimat
    suspend fun addUpdateReportKalimat(idUser: String,idStudent: String,reportHuruf: ReportKalimat): Boolean
    fun getAllReportKalimatFromFirestore(idUser: String, idStudent: String): Flow<Response<ReportKalimat>>

    //tulis huruf
    suspend fun createReportTulisHurufDataSets(idUser: String, idStudent: String): String
    suspend fun updateReportTulisHuruf(idUser: String,idStudent: String, reportTulisHuruf: ReportTulisHuruf): Boolean
    fun getAllReportTulisHurufFromFirestore(idUser: String, idStudent: String): Flow<Response<List<ReportTulisHuruf>>>

    //tulis angka
    suspend fun createReportAngkaDataSets(idUser: String,idStudent: String): String
    suspend fun addUpdateReportAngka(idUser: String,idStudent: String, reportTulisAngka: ReportTulisAngka): Boolean
    fun getAllReportAngkaFromFirestore(idUser: String, idStudent: String): Flow<Response<List<ReportTulisAngka>>>

    //tulis kata
    suspend fun createReportTulisKataDataSets(idUser: String,idStudent: String): String
    suspend fun addUpdateReportTulisKata(idUser: String, idStudent: String, reportTulisKata: ReportTulisKata): Boolean
    fun getAllReportTulisKataFromFirestore(idUser: String, idStudent: String): Flow<Response<List<ReportTulisKata>>>

    //Kuis Tulis
//    suspend fun addUpdateReportKuisTulis(idUser: String, idStudent: String, reportKuisTulis: ReportTulisQuiz): Boolean
//    fun getAllReportKuisTulisFromFirestore(idUser: String, idStudent: String): Flow<Response<List<ReportTulisQuiz>>>
}