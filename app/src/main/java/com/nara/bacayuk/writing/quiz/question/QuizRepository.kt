package com.example.tracingalphabet.quiz.question

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class QuizRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val quizCollection = firestore.collection("quizzes")

    fun addQuiz(quiz: Question, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val document = quizCollection.document()
        quiz.id = document.id

        document.set(quiz)
            .addOnSuccessListener {
                updateQuizSetQuestionCount(quiz.id)
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getQuizzesByQuizSetId(quizSetId: String, onSuccess: (List<Question>) -> Unit, onFailure: (Exception) -> Unit) {
        quizCollection
            .whereEqualTo("quizSetId", quizSetId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val questions = querySnapshot.toObjects(Question::class.java)
                    onSuccess(questions)
                }
            }
    }

    private fun updateQuizSetQuestionCount(quizSetId: String) {
        quizCollection
            .whereEqualTo("quizSetId", quizSetId)
            .get()
            .addOnSuccessListener { documents ->
                firestore.collection("quizSets")
                    .document(quizSetId)
                    .update("questionCount", documents.size())
            }
    }


    fun updateQuiz(quiz: Question, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        quizCollection.document(quiz.id)
            .set(quiz)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteQuiz(quizId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        quizCollection.document(quizId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
