package com.nara.bacayuk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tulis (
    var id: String = "",
    var suara: String = "",
    val tulisAngka: String = "",
    val tulisHuruf: String = "",
    val tulisKata: String = "",
    val tulisQuiz: String = "",
    val reportTulisAngka: ReportTulisAngka? = ReportTulisAngka(),
    val reportTulisHuruf: ReportTulisHuruf? = ReportTulisHuruf(),
    val reportTulisKata: ReportTulisKata? = ReportTulisKata(),
    val reportTulisQuiz: ReportTulisQuiz? = ReportTulisQuiz(),
    ): Parcelable

