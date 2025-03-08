package com.nara.bacayuk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportTulisHuruf (
    var tulisHuruf: String = "",
    var audioUrl: String = "",
    var materiTulisHurufKapital: Boolean = false,
    var materiTulisHurufNonKapital: Boolean = false,
    var latihanTulisHurufKapital: Boolean = false,
    var latihanTulisHurufNonKapital: Boolean = false,
): Parcelable