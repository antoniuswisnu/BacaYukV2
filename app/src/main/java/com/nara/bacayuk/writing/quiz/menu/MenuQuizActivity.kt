package com.nara.bacayuk.writing.quiz.menu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nara.bacayuk.writing.quiz.tracing.QuizAttemptActivity
import com.nara.bacayuk.writing.quiz.question.ListQuestionActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityMenuQuizBinding
import com.nara.bacayuk.databinding.DialogCreateQuizSetBinding
import com.nara.bacayuk.ui.feat_menu_utama.MainActivity

class MenuQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuQuizBinding
    private lateinit var adapter: MenuQuizAdapter
    private val firestore = FirebaseFirestore.getInstance()
    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)
                .apply {
                    putExtra("student", student)
                }
            )
        }

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        setupRecyclerView()
        setupClickListeners()
        loadQuizSets()
    }

    private fun setupRecyclerView() {
        adapter = MenuQuizAdapter(
            onMenuQuizClick = { quizSet ->
                AlertDialog.Builder(this)
                    .setTitle("Mulai Kuis")
                    .setMessage("Apakah Anda yakin ingin memulai mengerjakan soal?")
                    .setPositiveButton("Ya") { _, _ ->
                        val intent = Intent(this, QuizAttemptActivity::class.java)
                        intent.putExtra("quizSetId", quizSet.id)
                        intent.putExtra("student", student)
                        startActivity(intent)
                    }
                    .setNegativeButton("Tidak", null)
                    .show()
            },
            onDeleteClick = { quizSet ->
                deleteQuizSet(quizSet.id)
            },
            onEditClick = { quizSet ->
                val intent = Intent(this, ListQuestionActivity::class.java)
                intent.putExtra("quizSetId", quizSet.id)
                intent.putExtra("title", quizSet.title)
                intent.putExtra("student", student)
                startActivity(intent)
            }
        )

        binding.rvMenuQuiz.apply {
            layoutManager = LinearLayoutManager(this@MenuQuizActivity)
            adapter = this@MenuQuizActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnAddQuiz.setOnClickListener {
            showCreateQuizSetDialog()
        }
    }

    private fun showCreateQuizSetDialog() {
        val dialogBinding = DialogCreateQuizSetBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle("Buat Quiz Baru")
            .setView(dialogBinding.root)
            .setPositiveButton("Buat") { dialog, _ ->
                val title = dialogBinding.etTitle.text.toString()
                val description = dialogBinding.etDescription.text.toString()

                if (title.isNotBlank()) {
                    createQuizSet(title, description)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun createQuizSet(title: String, description: String) {
        val quizSet = MenuQuiz(
            id = firestore.collection("quizSets").document().id,
            title = title,
            description = description,
        )

        firestore.collection("quizSets")
            .document(quizSet.id)
            .set(quizSet)
            .addOnSuccessListener {
                loadQuizSets()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("MenuQuizActivity", "Error creating quiz set: ${e.message}")
            }
    }

    private fun loadQuizSets() {
        firestore.collection("quizSets")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val quizSets = result.toObjects(MenuQuiz::class.java)
                adapter.submitList(quizSets)

                binding.tvEmptyState.visibility =
                    if (quizSets.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.d("MenuQuizActivity", "Error loading quiz sets: ${e.message}")
            }
    }

    private fun deleteQuizSet(quizSetId: String) {
        firestore.runBatch { batch ->
            batch.delete(firestore.collection("quizSets").document(quizSetId))

            firestore.collection("quizzes")
                .whereEqualTo("quizSetId", quizSetId)
                .get()
                .addOnSuccessListener { questions ->
                    questions.documents.forEach { doc ->
                        batch.delete(doc.reference)
                    }
                }
        }.addOnSuccessListener {
            loadQuizSets()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}