package com.nara.bacayuk.writing.quiz.question

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    var id: String = "",
    val question: String = "",
    val correctAnswer: String = "",
    val questionType: String = "",
    val quizSetId: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable