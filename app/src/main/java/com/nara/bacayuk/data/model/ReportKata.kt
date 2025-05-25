package com.nara.bacayuk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportKata(
    val belajarVokal: BelajarVokal = BelajarVokal(),
    val quizSusunKata: ArrayList<SoalKata> = addSusunKata(),
    val quizPilganKata: ArrayList<SoalKata> = addLatihanBacaKata(),
) : Parcelable

fun addSusunKata(): ArrayList<SoalKata> {
    val list = arrayListOf<SoalKata>()
    list.add(
        SoalKata(
            1,
            "bola",
            "bo-la-ha",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%201.png?alt=media&token=c61f9297-d9ab-4848-8937-e77548a9ac84"
        )
    )
    list.add(
        SoalKata(
            2,
            "mata",
            "ma-ta-du",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%202.png?alt=media&token=4b5a415c-f91d-461e-91eb-c697f8d138c4"
        )
    )
    list.add(
        SoalKata(
            3,
            "baju",
            "ba-ju-re",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%203.png?alt=media&token=8881f29c-b181-4cd8-aad6-273edaaffa2a"
        )
    )
    list.add(
        SoalKata(
            4,
            "kaki",
            "ka-ki-ro",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%204.png?alt=media&token=5c4c1e25-3755-483d-80ce-616ff3b6b9b5"
        )
    )
    list.add(
        SoalKata(
            5,
            "meja",
            "me-ja-fa",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%205.png?alt=media&token=32ce03f8-499a-4fa2-ae05-dc6139afa2ad"
        )
    )
    list.add(
        SoalKata(
            6,
            "buku",
            "bu-ku-ti",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%206.png?alt=media&token=a12df1eb-a482-4069-b540-ef2424524262"
        )
    )
    list.add(
        SoalKata(
            7,
            "madu",
            "ma-du-pi",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%207.png?alt=media&token=69e618d8-eea0-4484-b051-a9db27addbba"
        )
    )
    list.add(
        SoalKata(
            8,
            "sepeda",
            "se-pe-da-ko",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%208.png?alt=media&token=14433da1-1173-4ce6-b4ed-8d83aad0a9b9"
        )
    )
    list.add(
        SoalKata(
            9,
            "sepatu",
            "se-pa-tu-li",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%209.png?alt=media&token=3bf12111-816e-4f90-8fa8-bf500fc09779"
        )
    )
    list.add(
        SoalKata(
            10,
            "celana",
            "ce-la-na-vo",
            false,
            "-",
            0,
            "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/susun-kata%2FsusunKata%20-%2010.png?alt=media&token=29a21686-94a8-4e2d-9273-168afbd99132"
        )
    )
    return list
}

fun addLatihanBacaKata(): ArrayList<SoalKata> {
    val list = arrayListOf<SoalKata>()
    list.add(SoalKata(1, "kue", "kue-topi-nasi", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%201.png?alt=media&token=33906701-6020-4d0f-a02e-af92ead96f6e"))
    list.add(SoalKata(2, "topi", "topi-kopi-sapi", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%202.png?alt=media&token=a6753b29-47fb-477b-a385-c2f780be119e"))
    list.add(SoalKata(3, "nasi", "nasi-roti-susu", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%203.png?alt=media&token=d1259152-de2f-46dc-8c66-fb215a61d82b"))
    list.add(SoalKata(4, "sapi", "sapi-topi-gula", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%204.png?alt=media&token=ed84428f-4a17-4ece-9f62-65c2e9f15b27"))
    list.add(SoalKata(5, "roti", "roti-kopi-nasi", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%205.png?alt=media&token=2c33faf8-a6a4-45c7-b94d-78ab26ee376b"))
    list.add(SoalKata(6, "susu", "susu-kopi-topi", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%206.png?alt=media&token=2c99ff4b-3c07-40b3-a8ad-452bbf60c606"))
    list.add(SoalKata(7, "kopi", "kopi-nasi-susu", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%207.png?alt=media&token=e8ce6dc9-693f-46a6-9d5b-fa84b895e977"))
    list.add(SoalKata(8, "boneka", "boneka-kamera-roti", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%208.png?alt=media&token=a8388861-970e-4b20-a264-74330f593987"))
    list.add(SoalKata(9, "kamera", "kamera-sepeda-sepatu", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%209.png?alt=media&token=8d689e21-0bab-4717-b36e-edc4d033413e"))
    list.add(SoalKata(10, "kuda", "kuda-sapi-kopi", false, "-", 0, "https://firebasestorage.googleapis.com/v0/b/bacayukv2.appspot.com/o/baca-kata%2FlatihanBacaKata%20-%2010.png?alt=media&token=cfc1d4f2-3d51-45c6-9271-5b0b4c5c30e0"))
    return list
}


@Parcelize
data class SoalKata(
    var level: Int = 1,
    var correctAnswer: String = "",
    //dipisah - //ex: ma-na-mu //kalo pilgan, sapi-babi-anjing
    var optionList: String = "",
    var isCorrect: Boolean = false,
    var studentAnswer: String = "",
    var alreadyDone: Int = 0,
    var imageUrl: String = ""
) : Parcelable

@Parcelize
data class BelajarVokal(
    var isADone: Boolean = false,
    var isIDone: Boolean = false,
    var isUDone: Boolean = false,
    var isEDone: Boolean = false,
    var isODone: Boolean = false,
) : Parcelable

@Parcelize
class BelajarSuku(
    var abjadName: String = "",
    val belajarVokal: BelajarVokal = BelajarVokal(),
    var AudioBelajarSuku: AudioBelajarSuku = AudioBelajarSuku()
) : Parcelable

//createDataSetKata
@Parcelize
data class AudioBelajarSuku(
    var abjadName: String = "",
    var audioA: String = "",
    var audioI: String = "",
    var audioU: String = "",
    var audioE: String = "",
    var audioO: String = "",
) : Parcelable

fun createDataSetAudioBelajarSuku(): List<AudioBelajarSuku> {
    return listOf(
        AudioBelajarSuku(
            abjadName = "Aa",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/A-a.m4a?alt=media&token=179462ac-6f01-4e5d-8e42-d10a492ea26d",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/A-i.m4a?alt=media&token=c8777e7c-182a-406d-b9f8-5ff58377e3b2",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/A-u.m4a?alt=media&token=17b580f6-da55-4a66-921c-a95959329220",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/A-e.m4a?alt=media&token=17250e68-e8a9-40a7-87d6-236dd145ef29",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/A-o.m4a?alt=media&token=c572c74e-4d87-4a88-93c4-ae53f3898da4"
        ),
        AudioBelajarSuku(
            abjadName = "Bb",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/B-a.m4a?alt=media&token=7524a03a-8dfe-4215-90ad-c88761b0e6d2",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/B-i.m4a?alt=media&token=57fcd913-57c6-40cd-9a8b-fae8d81ef50f",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/B-u.m4a?alt=media&token=f8fde6a2-05ef-4d17-af4d-8a0332286821",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/B-e.m4a?alt=media&token=d631956e-b045-4d47-acbc-dcbe78be5968",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/B-o.m4a?alt=media&token=e319da9e-d693-4e5c-a4a3-591b82a7eab0"
        ),
        AudioBelajarSuku(
            abjadName = "Cc",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/C-a.m4a?alt=media&token=a6140ed9-e622-48b2-af74-de22048a55f5",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/C-i.m4a?alt=media&token=5f9a9110-2200-4add-a802-048e10cbecaf",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/C-u.m4a?alt=media&token=cc5c51f3-ea83-4d8f-b166-0898255e204a",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/C-e.m4a?alt=media&token=93f4f1db-cef0-4aee-b59d-e346110da447",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/C-o.m4a?alt=media&token=9ac21bd3-df63-41a2-86f5-b50281273c1a"
        ),
        AudioBelajarSuku(
            abjadName = "Dd",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/D-a.m4a?alt=media&token=b58aa1db-2abb-4e9a-bd58-4b19343c1573",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/D-i.m4a?alt=media&token=8f904bdb-cde7-4244-9e0f-ce1799b3fe53",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/D-u.m4a?alt=media&token=5c48aae8-7433-4309-ac86-2c6f986d8f67",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/D-e.m4a?alt=media&token=9d3166df-29cd-4f39-8592-408cdaeef146",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/D-o.m4a?alt=media&token=d6a5288a-9d7a-45c5-96ba-030ad3c17593"
        ),
        AudioBelajarSuku(
            abjadName = "Ee",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/E-a.m4a?alt=media&token=135e7d55-9f4a-41dd-828a-95a4a6bc6ee9",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/E-i.m4a?alt=media&token=cfe7b742-b8b4-495a-81fa-ef64e6f99d91",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/E-u.m4a?alt=media&token=0d338a07-55ee-43c8-84a4-fdd09a51d442",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/E-e.m4a?alt=media&token=53d8c9bd-0c0a-499f-9c14-5b064db8a0da",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/E-o.m4a?alt=media&token=9b8e53ad-5140-45a1-a2f6-46532aa0361c"
        ),
        AudioBelajarSuku(
            abjadName = "Ff",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/F-a.m4a?alt=media&token=87665adf-b67a-46b2-975a-50d8f5c97169",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/F-i.m4a?alt=media&token=0fb98db1-c532-49c4-abb6-07389090b559",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/F-u.m4a?alt=media&token=bdedb2ca-af67-4587-abba-9fc402ce49e1",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/F-e.m4a?alt=media&token=b918f721-81e3-414d-bf57-2a26e39cdf0d",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/F-o.m4a?alt=media&token=033daf7b-2828-4137-8e78-25a0d03ea66b"
        ),
        AudioBelajarSuku(
            abjadName = "Gg",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/G-a.m4a?alt=media&token=e68a4c31-2598-481d-8837-562892fa963e",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/G-i.m4a?alt=media&token=b5c4ca46-2ad1-4a3e-8481-ee79b0265dbc",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/G-u.m4a?alt=media&token=fcf41994-271c-48ce-975e-2b6aabb8300e",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/G-e.m4a?alt=media&token=664a8fcd-587f-4134-a241-c30d17e585de",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/G-o.m4a?alt=media&token=a301b45c-a886-4b2c-a354-72ec2d81f1a5"
        ),
        AudioBelajarSuku(
            abjadName = "Hh",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/H-a.m4a?alt=media&token=06e610ae-057d-49aa-9177-d75310f3e075",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/H-i.m4a?alt=media&token=84eedb58-da88-4809-835c-611d80da4dc8",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/H-u.m4a?alt=media&token=b2745972-1670-4d99-8ec9-330f76da4882",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/H-e.m4a?alt=media&token=a49e9381-346b-4aad-8f4c-1741b12ee58c",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/H-o.m4a?alt=media&token=61057530-492c-42f4-a235-19ea942075bb"
        ),
        AudioBelajarSuku(
            abjadName = "Ii",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/I-a.m4a?alt=media&token=306f36e3-d727-4d9e-8833-fb95fbaa1ffc",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/I-i.m4a?alt=media&token=06da0124-02f3-4060-ba46-c0ab569d5cd7",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/I-u.m4a?alt=media&token=6d889d07-e531-46dc-abf1-9b2ca75c538f",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/I-e.m4a?alt=media&token=ef3b537c-b59e-46aa-a13d-6f6a437f2722",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/I-o.m4a?alt=media&token=f27b442c-c4c4-46aa-8a9e-173247e52197"
        ),
        AudioBelajarSuku(
            abjadName = "Jj",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/J-a.m4a?alt=media&token=aa55c201-d842-4f05-ba0d-484aec011456",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/J-i.m4a?alt=media&token=7070cdea-10c3-43f0-83e1-0ce5b7bb4fe1",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/J-u.m4a?alt=media&token=4d72c7aa-957e-40e8-ab50-ac721455363a",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/J-e.m4a?alt=media&token=3b379dcb-ee26-4bd7-b5ec-017cad30d23d",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/J-o.m4a?alt=media&token=93dbd12d-b7c4-4154-ac6e-b59ee88a0bf9"
        ),
        AudioBelajarSuku(
            abjadName = "Kk",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/K-a.m4a?alt=media&token=00d9f53c-cb33-4b51-8014-7eceae6cf042",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/K-i.m4a?alt=media&token=abfa3ab1-cbb9-4bea-8508-2136fe7b7771",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/K-u.m4a?alt=media&token=f2f0e1c0-bd82-468d-af4e-ebb05af001b0",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/K-e.m4a?alt=media&token=cf3f816d-5595-4784-8d72-52ca31ae8bdc",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/K-o.m4a?alt=media&token=beb70c23-ad16-448b-a57b-1cc3041f26b1"
        ),
        AudioBelajarSuku(
            abjadName = "Ll",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/L-a.m4a?alt=media&token=603aaeb2-9688-4475-8890-54765de9d1b2",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/L-i.m4a?alt=media&token=c37be7b9-7663-47c2-9cd8-f48715deec40",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/L-u.m4a?alt=media&token=0a8946a6-224e-4f2f-9742-5344b7872ff8",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/L-e.m4a?alt=media&token=4843e341-8199-49c8-8423-3133e85828ad",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/L-o.m4a?alt=media&token=4479a75b-a8c2-4991-af4b-45e590906473"
        ),
        AudioBelajarSuku(
            abjadName = "Mm",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/M-a.m4a?alt=media&token=5a0278b9-58f5-44db-87be-086ba7f5ee1c",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/M-i.m4a?alt=media&token=53918533-3799-4255-a3ba-2e2d3ab6f5e0",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/M-u.m4a?alt=media&token=66104943-1c9f-4e70-bdc1-59b27a1b4aa8",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/M-e.m4a?alt=media&token=5a07f3ec-5f34-4401-8985-0b728c5a6d41",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/M-o.m4a?alt=media&token=b67c5858-7375-42f8-9260-24c84b27b5e8"
        ),
        AudioBelajarSuku(
            abjadName = "Nn",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/N-a.m4a?alt=media&token=49227ea4-6c82-408e-b397-18bf4cb2c6f5",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/N-i.m4a?alt=media&token=52b2691b-cd4c-4ce0-b665-91172f069d73",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/N-u.m4a?alt=media&token=d3268449-da2b-4110-8d64-6bbaefb32998",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/N-e.m4a?alt=media&token=b64cff5c-2eb9-4984-85dd-8d5bc7c81d08",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/N-o.m4a?alt=media&token=85c4f9cb-3299-475f-aaf3-cc05b79da1d1"
        ),
        AudioBelajarSuku(
            abjadName = "Oo",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/O-a.m4a?alt=media&token=c55b7355-ab2a-4db8-a1da-51c1262d3e78",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/O-i.m4a?alt=media&token=f988ff49-fb13-4c09-a19d-9f7f2e13c282",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/O-u.m4a?alt=media&token=d1d38a35-d2e5-4469-9301-5a1629fa3895",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/O-e.m4a?alt=media&token=80f6cfe4-469f-4c22-a5bd-5d67a7a1b852",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/O-o.m4a?alt=media&token=0b911271-6365-4f0e-8877-d4da7b281478"
        ),
        AudioBelajarSuku(
            abjadName = "Pp",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/P-a.m4a?alt=media&token=3d25dfdc-4442-466f-8cfd-7fadf52033e3",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/P-i.m4a?alt=media&token=3b392e7f-e647-4719-89e0-92c70366659f",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/P-u.m4a?alt=media&token=fcb92e36-ba6e-4345-8125-e5e8e171ab8f",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/P-e.m4a?alt=media&token=7dca0435-2dca-48aa-841c-765a8f3de942",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/P-o.m4a?alt=media&token=85d4f708-8872-4bc3-bd39-c4b2f5afd631"
        ),
        AudioBelajarSuku(
            abjadName = "Qq",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Q-a.m4a?alt=media&token=986a0377-1331-4624-ad87-46ff71544742",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Q-i.m4a?alt=media&token=d6f7d491-7598-46ad-98ac-0b43b0199ecb",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Q-u.m4a?alt=media&token=99363354-291d-4921-abb5-f23ca4b3b59e",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Q-e.m4a?alt=media&token=c943615c-b0b2-460c-b685-baa02234370f",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Q-o.m4a?alt=media&token=3dc71d4f-77c2-4a6c-bfca-22107be5d06c"
        ),
        AudioBelajarSuku(
            abjadName = "Rr",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/R-a.m4a?alt=media&token=79ff5530-a79a-4b94-aaab-fec1c5e152fc",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/R-i.m4a?alt=media&token=44b4837d-6dc2-41ca-8f3b-32ba419a583e",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/R-u.m4a?alt=media&token=b7ace215-f53a-4f77-b2a6-0162913b9b5d",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/R-e.m4a?alt=media&token=aad909f7-9aa5-45f3-a0b4-d951ffecd0aa",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/R-o.m4a?alt=media&token=c0cad639-755b-4453-b4d0-b936e6db09bd"
        ),
        AudioBelajarSuku(
            abjadName = "Ss",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/S-a.m4a?alt=media&token=97e72594-a534-4cec-b87c-5e43b73d790c",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/S-i.m4a?alt=media&token=8164b7a3-b9aa-483e-8d8c-e7c0dd8be61e",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/S-u.m4a?alt=media&token=1528619f-cdf5-454e-96fb-6d29e2dff233",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/S-e.m4a?alt=media&token=81ecdcf1-d9ec-45d7-97d4-ab3dd32d5975",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/S-o.m4a?alt=media&token=6de6d974-0c87-420e-a568-e74062b3dfd9"
        ),
        AudioBelajarSuku(
            abjadName = "Tt",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/T-a.m4a?alt=media&token=1df5c83a-0f9c-45f4-85f6-9299b0966826",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/T-i.m4a?alt=media&token=0cda20b1-d390-4c2a-b99b-20cb3f758992",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/T-u.m4a?alt=media&token=e7124ca4-b35d-400a-801b-4a0dc58b35ab",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/T-e.m4a?alt=media&token=96d565cb-2116-4351-a37a-6c5a1cdfe87a",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/T-o.m4a?alt=media&token=ea5633e4-7842-498b-97d2-b455b1c4e644"
        ),
        AudioBelajarSuku(
            abjadName = "Uu",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/U-a.m4a?alt=media&token=8f6e8d23-6946-4cd0-a994-c7ebeabc5432",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/U-i.m4a?alt=media&token=0c4dd83a-ad68-4287-86f0-e25c0cf0e1d1",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/U-u.m4a?alt=media&token=6d8bcfea-4f9e-4128-9531-cc188df1fe81",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/U-e.m4a?alt=media&token=42e51d9f-7562-4d8d-9d7c-db3a5c4f1657",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/U-o.m4a?alt=media&token=58958702-87e3-4bdd-a497-db2952ac2ee0"
        ),
        AudioBelajarSuku(
            abjadName = "Vv",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/V-a.m4a?alt=media&token=897a3c57-ea12-4075-a74e-fd2b8e26fee9",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/V-i.m4a?alt=media&token=4d9e3f0d-8b8e-4c00-aff8-380bea4e68d2",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/V-u.m4a?alt=media&token=7330ab1f-def6-4005-86ef-faa1098220b1",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/V-e.m4a?alt=media&token=56cfd221-dffa-4304-906f-bd44209746e6",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/V-o.m4a?alt=media&token=1b7531d0-8b1e-4e6c-a777-0c756cf1fcb5"
        ),
        AudioBelajarSuku(
            abjadName = "Ww",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/W-a.m4a?alt=media&token=166e1464-5d53-4276-8d03-23291eef0f24",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/W-i.m4a?alt=media&token=38b4e1a3-478b-4e69-b95a-5181722d33f9",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/W-u.m4a?alt=media&token=990ce732-582f-4f0d-96ba-e988428b57f9",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/W-e.m4a?alt=media&token=10a052c7-6f48-4076-bd07-720e9f18ed99",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/W-o.m4a?alt=media&token=fd2f2a19-a785-4a11-90a0-41ad65ff6994"
        ),
        AudioBelajarSuku(
            abjadName = "Xx",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/X-a.m4a?alt=media&token=3c5209b4-8830-48a8-b87e-787d4cd7917a",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/X-i.m4a?alt=media&token=6570693d-fb64-4999-9b8c-330a020f6bbf",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/X-u.m4a?alt=media&token=b5b285d8-c9b9-42b5-81bf-70ddca4ee3d4",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/X-e.m4a?alt=media&token=69f8fd71-29d2-4614-a991-02166568c0cf",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/X-o.m4a?alt=media&token=5c8a4ef5-e84b-4b41-bcef-6f9702d4fcdc"
        ),
        AudioBelajarSuku(
            abjadName = "Yy",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Y-a.m4a?alt=media&token=f958c07c-ec73-4dc6-b771-b363d44a2550",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Y-i.m4a?alt=media&token=3ae34e39-108f-4060-8e76-ca78268c77aa",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Y-u.m4a?alt=media&token=d060fb4f-e8fb-424c-a6c9-b973c5497803",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Y-e.m4a?alt=media&token=31498392-c317-4625-b6e1-604bc750c570",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Y-o.m4a?alt=media&token=79f01483-3180-4f9f-b559-f485c560ba22"
        ),
        AudioBelajarSuku(
            abjadName = "Zz",
            audioA = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Z-a.m4a?alt=media&token=d53f0290-d56a-4216-8c8a-fe6636dfa8a5",
            audioI = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Z-i.m4a?alt=media&token=e20c03ad-1f8c-40b8-8613-de55fad0bf02",
            audioU = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Z-u.m4a?alt=media&token=51bf674a-be30-464c-836d-9832a5fceb9c",
            audioE = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Z-e.m4a?alt=media&token=648a69fa-a2ed-46f7-bb36-59ebb6dc9f9d",
            audioO = "https://firebasestorage.googleapis.com/v0/b/bisabelajar-b0579.appspot.com/o/Z-o.m4a?alt=media&token=aae74c8c-86d5-46c4-8720-445980b186de"
        )

    )
}