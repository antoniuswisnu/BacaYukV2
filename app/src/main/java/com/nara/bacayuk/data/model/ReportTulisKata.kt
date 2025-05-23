package com.nara.bacayuk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportTulisKata (
    var id: String = "", // ID Dokumen dari Firestore
    var tulisKata: String = "",
    var level: String = "", // "Mudah", "Sedang", "Tinggi"
    var audioUrl: String = "", // Field yang sudah ada
    var materiTulisKata: Boolean = false, // Field yang sudah ada
    var latihanTulisKata: Boolean = false, // Field yang sudah ada
): Parcelable {
    // Konstruktor tanpa argumen diperlukan untuk deserialisasi Firestore
    constructor() : this("", "", "", "", false, false)
}
