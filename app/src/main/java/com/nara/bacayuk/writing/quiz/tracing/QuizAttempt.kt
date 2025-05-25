package com.nara.bacayuk.writing.quiz.tracing

import android.os.Parcelable
import com.nara.bacayuk.writing.quiz.predict.QuizAnswer
import kotlinx.android.parcel.Parcelize

data class QuizAttempt(
    var id: String = "",
    val quizSetId: String = "",
    val answers: List<QuizAnswer> = emptyList(),
    val startTime: Long = 0,
    var endTime: Long = 0,
    var score: Int = 0
)

@Parcelize
data class QuizAttemptDetail(
    val questionText: String,
    val correctAnswer: String,
    val userAnswerPredicted: String,
    val isCorrect: Boolean
) : Parcelable
