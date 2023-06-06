package com.nara.bacayuk.domain.repository

import com.nara.bacayuk.data.model.*
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun createReportHurufDataSets(idUser: String,idStudent: String): Boolean
    suspend fun updateReportHuruf(idUser: String,idStudent: String,reportHuruf: ReportHuruf): Boolean
    fun getAllReportFromFirestore(idUser: String, idStudent: String): Flow<Response<List<ReportHuruf>>>

    suspend fun createReportKataDataSets(idUser: String,idStudent: String): Boolean
    suspend fun updateReportKata(idUser: String,idStudent: String,reportHuruf: ReportKata): Boolean
    fun getAllReportKataFromFirestore(idUser: String, idStudent: String): Flow<Response<ReportKata>>
    fun getAllBelajarVokal(idUser: String, idStudent: String): Flow<Response<List<BelajarSuku>>>

    suspend fun addUpdateReportKalimat(idUser: String,idStudent: String,reportHuruf: ReportKalimat): Boolean
    fun getAllReportKalimatFromFirestore(idUser: String, idStudent: String): Flow<Response<ReportKalimat>>
}