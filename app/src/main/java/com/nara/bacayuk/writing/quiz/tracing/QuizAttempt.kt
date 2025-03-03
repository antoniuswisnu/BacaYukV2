package com.nara.bacayuk.writing.quiz.tracing

import com.nara.bacayuk.writing.quiz.predict.QuizAnswer

data class QuizAttempt(
    var id: String = "",
    val quizSetId: String = "",
    val answers: List<QuizAnswer> = emptyList(),
    val startTime: Long = 0,
    var endTime: Long = 0,
    var score: Int = 0
)