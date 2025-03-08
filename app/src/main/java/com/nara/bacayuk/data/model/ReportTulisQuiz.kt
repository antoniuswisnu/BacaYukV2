package com.nara.bacayuk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportTulisQuiz (
    var tulisQuiz: String = "",
    var audioUrl: String = "",
    var tipeQuiz: String = "",
    var jawabanQuiz: String = "",
    var jawabanBenar: Boolean = false,
): Parcelable