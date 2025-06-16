package com.nara.bacayuk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportKalimat (
        val quizSusunKata: ArrayList<SoalKata> = addSusunKalimat(),
        val quizPilganKata: ArrayList<SoalKata> = addLatihanBacaKalimat(),
): Parcelable

fun addSusunKalimat(): ArrayList<SoalKata> {
        val list = arrayListOf<SoalKata>()
        list.add(SoalKata(1, "ibu sapu lantai", "ibu-sapu-lantai", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%201.png?alt=media&token=dcd539dd-c1f2-4e8b-9c23-98eb3e620f66"))
        list.add(SoalKata(2, "ayah siram bunga", "ayah-siram-bunga", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%202.png?alt=media&token=3a02882a-cc0b-4b2d-a1d0-0c09c770c8a7"))
        list.add(SoalKata(3, "kakak baca buku", "kakak-baca-buku", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%203.png?alt=media&token=6472db17-52c5-44a2-8e3b-95bdb09979a6"))
        list.add(SoalKata(4, "adik pakai baju", "adik-pakai-baju", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%204.png?alt=media&token=94b0bb94-4742-4008-8075-0c4b111e232a"))
        list.add(SoalKata(5, "ibu pakai sepatu", "ibu-pakai-sepatu", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%205.png?alt=media&token=582c4e2e-8ba5-4263-af1f-cafbaa0cfe9f"))
        list.add(SoalKata(6, "ibu makan roti", "ibu-makan-roti", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%206.png?alt=media&token=9dc8d3af-ed95-46c5-95e5-6275f9bec956"))
        list.add(SoalKata(7, "adik makan kue", "adik-makan-kue", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%207.png?alt=media&token=fc79e8c8-9dcb-4e8c-839e-60a2c167bdb2"))
        list.add(SoalKata(8, "adik naik sepeda", "adik-naik-sepeda", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%208.png?alt=media&token=6fa27a71-c896-486c-827c-c78a33297016"))
        list.add(SoalKata(9, "ayah sedang berlari", "ayah-sedang-berlari", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%209.png?alt=media&token=c5b6e243-413d-4a27-980c-2fa251e92115"))
        list.add(SoalKata(10, "adik minum susu", "adik-minum-susu", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kalimat%2F3%20-%20susunKalimat%20-%2010.png?alt=media&token=c452e3c9-4bba-4d2d-9308-cd15288b92c4"))
        return list
}

fun addLatihanBacaKalimat(): ArrayList<SoalKata> {
        val list = arrayListOf<SoalKata>()
        list.add(
                SoalKata(
                        1,
                        "adik main boneka",
                        "adik main boneka-kakak makan buah-ayah sedang berlari",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%201.png?alt=media&token=7597a1a4-5a1f-4aba-a593-5a854f3f506e"
                )
        )
        list.add(
                SoalKata(
                        2,
                        "ibu siram bunga",
                        "ibu siram bunga-kakak baca buku-adik main bola",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%202.png?alt=media&token=5f3e874c-c4b8-44bf-8251-e3d4d9d1a205"
                )
        )
        list.add(
                SoalKata(
                        3,
                        "ayah pakai sepatu",
                        "ayah pakai sepatu-ibu makan roti-kakak main boneka",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%203.png?alt=media&token=5a1f9fb5-fcf5-4b15-b7a9-6a5bda9d19e9"
                )
        )
        list.add(
                SoalKata(
                        4,
                        "ibu baca buku",
                        "ibu baca buku-ayah naik sepeda-adik makan es krim",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%204.png?alt=media&token=5e39f778-a5bb-4cf6-9a05-288e1f6aa856"
                )
        )
        list.add(
                SoalKata(
                        5,
                        "ayah pakai topi",
                        "ayah pakai topi-adik makan roti-kakak main bola",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%205.png?alt=media&token=b1f4bb7b-76ec-4e8f-a6a2-c9613a40ab40"
                )
        )
        list.add(
                SoalKata(
                        6,
                        "adik sedang berlari",
                        "adik sedang berlari-kakak makan roti-ayah siram bunga",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%207.png?alt=media&token=9f5790dd-5fd6-4ce3-bcf7-9c03b9dca8ce"
                )
        )
        list.add(
                SoalKata(
                        7,
                        "ayah naik sepeda",
                        "ayah naik sepeda-ibu makan kue-kakak main bola",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%206.png?alt=media&token=1746342e-8fa2-4b29-99b2-83f1df5aff66"
                )
        )
        list.add(
                SoalKata(
                        8,
                        "adik main bola",
                        "adik main bola-kakak siram bunga-ayah baca buku",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%208.png?alt=media&token=cf7af888-b9d4-4761-8ca9-384fa2d15b70"
                )
        )
        list.add(
                SoalKata(
                        9,
                        "ayah baca koran",
                        "ayah baca koran-ayah main bola-ibu siram bunga",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%209.png?alt=media&token=3ea9ab3e-0640-47eb-9dd5-1ea426af3756"
                )
        )
        list.add(
                SoalKata(
                        10,
                        "adik makan es krim",
                        "adik makan es krim-ibu makan roti-ayah pakai topi",
                        false,
                        "-",
                        0,
                        "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kalimat%2F4%20-%20latihanBacaKalimat%20-%2010.png?alt=media&token=89ce957c-ac22-4f66-9c8b-fd950e1d972b"
                )
        )
        return list
}