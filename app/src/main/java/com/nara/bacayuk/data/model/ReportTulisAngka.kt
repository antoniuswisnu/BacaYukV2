package com.nara.bacayuk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportTulisAngka (
    val tulisName: String = "",
    var audioUrl: String = "",
    var materiAngka: Boolean = false,
    var latihanAngka: Boolean = false,
): Parcelable

