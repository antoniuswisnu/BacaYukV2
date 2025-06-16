package com.nara.bacayuk.writing.quiz.question

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class QuizRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private fun getStudentCollection(userId: String, studentId: String) =
        firestore.collection("Users").document(userId)
            .collection("Students").document(studentId)

    fun addQuiz(userId: String, studentId: String, quiz: Question, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val document = getStudentCollection(userId, studentId).collection("quizzes").document()
        quiz.id = document.id

        document.set(quiz)
            .addOnSuccessListener {
                // After adding a quiz, update the question count in the corresponding quizSet
                updateQuizSetQuestionCount(userId, studentId, quiz.quizSetId)
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getQuizzesByQuizSetId(userId: String, studentId: String, quizSetId: String, onSuccess: (List<Question>) -> Unit, onFailure: (Exception) -> Unit) {
        getStudentCollection(userId, studentId)
            .collection("quizzes")
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

    private fun updateQuizSetQuestionCount(userId: String, studentId: String, quizSetId: String) {
        val quizzesCollection = getStudentCollection(userId, studentId).collection("quizzes")
        val quizSetDocument = getStudentCollection(userId, studentId).collection("quizSets").document(quizSetId)

        quizzesCollection
            .whereEqualTo("quizSetId", quizSetId)
            .get()
            .addOnSuccessListener { documents ->
                quizSetDocument.update("questionCount", documents.size())
            }
    }

    fun updateQuiz(userId: String, studentId: String, quiz: Question, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        getStudentCollection(userId, studentId)
            .collection("quizzes")
            .document(quiz.id)
            .set(quiz)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteQuiz(userId: String, studentId: String, quizId: String, quizSetId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        getStudentCollection(userId, studentId)
            .collection("quizzes")
            .document(quizId)
            .delete()
            .addOnSuccessListener {
                // After deleting a quiz, update the question count
                updateQuizSetQuestionCount(userId, studentId, quizSetId)
                onSuccess()
            }
            .addOnFailureListener { onFailure(it) }
    }
}