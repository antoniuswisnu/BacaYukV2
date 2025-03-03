package com.example.tracingalphabet.quiz.predict

data class QuizAttempt(
    var id: String = "",
    val quizSetId: String = "",
    val answers: List<QuizAnswer> = emptyList(),
    val startTime: Long = 0,
    var endTime: Long = 0,
    var score: Int = 0
)