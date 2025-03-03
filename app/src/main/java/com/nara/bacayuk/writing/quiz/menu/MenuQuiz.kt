package com.nara.bacayuk.writing.quiz.menu

data class MenuQuiz (
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var questionCount: Int = 0
)