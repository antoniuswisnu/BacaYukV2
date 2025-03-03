package com.nara.bacayuk.writing.quiz.predict

data class QuizAnswer(
    val questionId: String = "",
    val userAnswer: String = "",
    var isCorrect: Boolean = false
)