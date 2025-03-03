package com.example.tracingalphabet.quiz.predict

data class QuizAnswer(
    val questionId: String = "",
    val userAnswer: String = "",
    var isCorrect: Boolean = false
)