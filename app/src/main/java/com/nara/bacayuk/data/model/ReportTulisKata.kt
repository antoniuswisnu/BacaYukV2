package com.nara.bacayuk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportTulisKata (
    var id: String = "",
    var tulisKata: String = "",
    var level: String = "",
    var audioUrl: String = "",
    var materiTulisKata: Boolean = false,
    var latihanTulisKata: Boolean = false,
): Parcelable {
    constructor() : this("", "", "", "", false, false)
}
