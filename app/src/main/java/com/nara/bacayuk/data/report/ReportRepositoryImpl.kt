package com.nara.bacayuk.data.report

import com.nara.bacayuk.data.model.*
import com.nara.bacayuk.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow

class ReportRepositoryImpl(private val dataSource: ReportDataSource) :ReportRepository {

    // Baca Huruf
    override suspend fun createReportHurufDataSets(idUser: String, idStudent: String): String {
        return dataSource.createReportHurufDataSets(idUser, idStudent)
    }

    override fun getAllReportFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportHuruf>>> {
        return dataSource.getAllReportFromFirestore(idUser, idStudent)
    }

    override suspend fun updateReportHuruf(
        idUser: String,
        idStudent: String,
        reportHuruf: ReportHuruf
    ): Boolean {
        return dataSource.updateReportHuruf(idUser, idStudent, reportHuruf)
    }

    // Baca Kata
    override suspend fun createReportKataDataSets(idUser: String, idStudent: String): String {
        return dataSource.createReportKataDataSets(idUser, idStudent)
    }

    override fun getAllReportKataFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<ReportKata>> {
        return dataSource.getAllReportKataFromFirestore(idUser, idStudent)
    }

    override suspend fun updateReportKata(
        idUser: String,
        idStudent: String,
        reportHuruf: ReportKata
    ): Boolean {
        return dataSource.updateReportKata(idUser, idStudent, reportHuruf)
    }

    override fun getAllBelajarVokal(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<BelajarSuku>>> {
        return dataSource.getAllBelajarVokal(idUser, idStudent)
    }

    override suspend fun updateBelajarSuku(
        idUser: String,
        idStudent: String,
        reportHuruf: BelajarSuku
    ): Boolean {
        return dataSource.updateBelajarSuku(idUser, idStudent, reportHuruf)
    }

    // Baca Kalimat
    override fun getAllReportKalimatFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<ReportKalimat>> {
        return dataSource.getAllReportKalimatFromFirestore(idUser, idStudent)
    }

    override suspend fun addUpdateReportKalimat(
        idUser: String,
        idStudent: String,
        reportHuruf: ReportKalimat
    ): Boolean {
        return dataSource.addUpdateReportKalimat(idUser, idStudent, reportHuruf)
    }

    // Tulis Angka
    override suspend fun createReportAngkaDataSets(idUser: String, idStudent: String): String {
        return dataSource.createReportAngkaDataSets(idUser, idStudent)
    }

    override suspend fun getAllReportTulisAngkaFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportTulisAngka>>> {
        return dataSource.getAllReportAngkaFromFirestore(idUser, idStudent)
    }

    override suspend fun addUpdateReportTulisAngka(
        idUser: String,
        idStudent: String,
        reportTulisAngka: ReportTulisAngka
    ): Boolean {
        return dataSource.addUpdateReportAngka(idUser, idStudent, reportTulisAngka)
    }

    // Tulis Huruf
    override suspend fun createReportTulisHurufDataSets(idUser: String, idStudent: String): String {
        return dataSource.createReportTulisHurufDataSets(idUser, idStudent)
    }

    override suspend fun getAllReportTulisHurufFromFirestore(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportTulisHuruf>>> {
        return dataSource.getAllReportTulisHurufFromFirestore(idUser, idStudent)
    }

    override suspend fun addUpdateReportTulisHuruf(
        idUser: String,
        idStudent: String,
        reportTulisHuruf: ReportTulisHuruf
    ): Boolean {
        return dataSource.updateReportTulisHuruf(idUser, idStudent, reportTulisHuruf)
    }

    // Tulis Kata
    override fun getAllReportTulisKata(
        idUser: String,
        idStudent: String
    ): Flow<Response<List<ReportTulisKata>>> {
        return dataSource.getAllReportTulisKata(idUser, idStudent)
    }

    override suspend fun addReportTulisKata(
        idUser: String,
        idStudent: String,
        reportTulisKata: ReportTulisKata
    ): Flow<Response<String>> {
        return dataSource.addReportTulisKata(idUser, idStudent, reportTulisKata)
    }

    override suspend fun updateReportTulisKata(
        idUser: String,
        idStudent: String,
        reportTulisKata: ReportTulisKata
    ): Flow<Response<Boolean>> {
        return dataSource.updateReportTulisKata(idUser, idStudent, reportTulisKata)
    }

    override suspend fun deleteReportTulisKata(
        idUser: String,
        idStudent: String,
        wordId: String
    ): Flow<Response<Boolean>> {
        return dataSource.deleteReportTulisKata(idUser, idStudent, wordId)
    }
}

