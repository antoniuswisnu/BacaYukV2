package com.nara.bacayuk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tulis (
    var id: String = "",
    var suara: String = "",
    val tulisName: String = "",
    val reportTulisAngka: ReportTulisAngka? = ReportTulisAngka(),
//    val reportTulisHuruf: ReportTulisHuruf? = ReportTulisHuruf(),
//    val reportTulisKata: ReportTulisKata? = ReportTulisKata(),
//    val reportTulisKuis: ReportTulisKuis? = ReportTulisKuis(),
    ): Parcelable