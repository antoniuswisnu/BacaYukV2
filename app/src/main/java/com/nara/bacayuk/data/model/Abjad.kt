package com.nara.bacayuk.data.model

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

@Parcelize
data class Abjad(
    var id: String = "",
    var abjadNonKapital: String = "",
    var abjadKapital: String = "",
    var suara: String = "",
    val abjadName: String = "",
    val belajarSuku: BelajarSuku? = BelajarSuku(),
    val reportHuruf: ReportHuruf? = ReportHuruf(),
    val reportKata: ReportKata? = ReportKata(),

): Parcelable

fun getAllDataAbjadNonKapital(): List<String>{
    return listOf("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u",
            "v","w","x","y","z")
}

data class AudioAbjad(
    var abjadName: String = "",
    var urlAudio: String = ""
)

fun generateAudioAbjad(): List<AudioAbjad> {
    return listOf(
        AudioAbjad(
            abjadName = "Aa",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20A.m4a?alt=media&token=499b04e1-e499-4279-ac82-89d085e36a44"
        ),
        AudioAbjad(
            abjadName = "Bb",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20B.m4a?alt=media&token=c00cbff4-7c36-4231-99c2-d62f92e83ad9"
        ),
        AudioAbjad(
            abjadName = "Cc",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20C.m4a?alt=media&token=9d08ea01-7831-4647-8b20-5167376e35bc"
        ),
        AudioAbjad(
            abjadName = "Dd",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20D.m4a?alt=media&token=8618c3a3-8c1e-4474-837d-e6d82eba050b"
        ),
        AudioAbjad(
            abjadName = "Ee",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20E.m4a?alt=media&token=f0f726f4-6707-4a47-b3fb-0f5a9cc24167"
        ),
        AudioAbjad(
            abjadName = "Ff",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20F.m4a?alt=media&token=baaaaeb2-c6ea-474c-908b-09570b1d0928"
        ),
        AudioAbjad(
            abjadName = "Gg",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20G.m4a?alt=media&token=6b44965c-ab45-439c-8b3a-b009d907d304"
        ),
        AudioAbjad(
            abjadName = "Hh",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20H.m4a?alt=media&token=a23c934e-8030-43ca-b609-e3f37fb1bcda"
        ),
        AudioAbjad(
            abjadName = "Ii",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20I.m4a?alt=media&token=9b10c553-e0c0-40f0-b498-094cf9295c04"
        ),
        AudioAbjad(
            abjadName = "Jj",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20J.m4a?alt=media&token=6a295f78-8db4-4d7a-b166-c3443a6f5878"
        ),
        AudioAbjad(
            abjadName = "Kk",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20K.m4a?alt=media&token=86156ce4-5c5a-4208-9329-953caed1dea0"
        ),
        AudioAbjad(
            abjadName = "Ll",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20L.m4a?alt=media&token=95beb9f2-65cf-4a56-9146-9d7dd4c2d6d5"
        ),
        AudioAbjad(
            abjadName = "Mm",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20M.m4a?alt=media&token=a88f0066-c132-4ead-bbaf-60bb357d0f2d"
        ),
        AudioAbjad(
            abjadName = "Nn",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20N.m4a?alt=media&token=9b56a963-1904-4ba3-802b-3a0ddb1531d1"
        ),
        AudioAbjad(
            abjadName = "Oo",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20O.m4a?alt=media&token=cb7b3958-bb38-4175-ba4e-4e6417cef6ee"
        ),
        AudioAbjad(
            abjadName = "Pp",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20P.m4a?alt=media&token=d2468c49-cbf0-4f6b-9222-b9a95aed25df"
        ),
        AudioAbjad(
            abjadName = "Qq",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20Q.m4a?alt=media&token=dfa9bb40-af85-4b46-98b1-1c18891ec5c2"
        ),
        AudioAbjad(
            abjadName = "Rr",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20R.m4a?alt=media&token=eae2a12a-987d-4502-9d99-efdd5f01eba5"
        ),
        AudioAbjad(
            abjadName = "Ss",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20S.m4a?alt=media&token=5098a11e-4340-44fc-8467-a3e73ca78349"
        ),
        AudioAbjad(
            abjadName = "Tt",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20T.m4a?alt=media&token=686390e7-4f83-4078-846f-786608ea7b5c"
        ),
        AudioAbjad(
            abjadName = "Uu",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20U.m4a?alt=media&token=0757db6e-d39a-48ee-a5cd-7c81c044016c"
        ),
        AudioAbjad(
            abjadName = "Vv",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20V.m4a?alt=media&token=31d317b6-60f4-425c-ae18-a2b5ee87e424"
        ),
        AudioAbjad(
            abjadName = "Ww",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20W.m4a?alt=media&token=c2bddf32-16b0-4763-bcdf-abda09f0db0a"
        ),
        AudioAbjad(
            abjadName = "Xx",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20X.m4a?alt=media&token=76db5946-c515-46ca-8266-5cb69124e6bd"
        ),
        AudioAbjad(
            abjadName = "Yy",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20Y.m4a?alt=media&token=152e6037-cf10-49c7-8e48-8b6968c657d6"
        ),
        AudioAbjad(
            abjadName = "Zz",
            urlAudio = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Huruf%20Z.m4a?alt=media&token=b3c76616-238c-4aa4-8f80-3b410d623f53"
        )
    )
}

fun getAllDataAbjadNonKapitalNonVokal(): List<String>{
    return listOf("b","c","d","f","g","h","j","k","l","m","n","o","p","q","r","s","t",
        "v","w","x","y","z")
}

fun getAllDataAbjadKapital(): List<String>{
    return listOf("A","B","C","D","E","F","G","H","I","J",
            "K","L","M","N","O","P","Q","R","S","T","U",
            "V","W","X","Y","Z")
}

fun getTwoRandomAbjadNonKapital(tidakDiambil: String): List<String> {
    val allAbjad = getAllDataAbjadNonKapital().toMutableList()
    //remove value tidak diambil
    allAbjad.remove(tidakDiambil)
    Log.d("tidakDiambil", "$allAbjad $tidakDiambil")
    val randomIndices = (allAbjad.indices).shuffled().take(2)
    return randomIndices.map { allAbjad[it] }
}

fun getTwoRandomAbjadKapital(tidakDiambil: String): List<String> {
    val allAbjad = getAllDataAbjadKapital().toMutableList()
    //remove value tidak diambil
    allAbjad.remove(tidakDiambil)
    Log.d("tidakDiambil", "$allAbjad $tidakDiambil")
    val randomIndices = (allAbjad.indices).shuffled().take(2)
    return randomIndices.map { allAbjad[it] }
}