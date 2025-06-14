package com.nara.bacayuk.writing.quiz.menu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.nara.bacayuk.writing.quiz.tracing.QuizAttemptActivity
import com.nara.bacayuk.writing.quiz.question.ListQuestionActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityMenuQuizBinding
import com.nara.bacayuk.databinding.DialogCreateQuizSetBinding
import com.nara.bacayuk.ui.custom_view.ConfirmationDialog
import com.nara.bacayuk.ui.custom_view.ConfirmationDialogRedStyle
import com.nara.bacayuk.ui.feat_menu_utama.MainActivity
import com.nara.bacayuk.utils.invisible

class MenuQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuQuizBinding
    private lateinit var adapter: MenuQuizAdapter
    private val firestore = FirebaseFirestore.getInstance()
    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply{
            toolbarAction.apply {
                imgActionRight.invisible()
                rootView.backgroundTintList = AppCompatResources.getColorStateList(this@MenuQuizActivity,
                    R.color.purple_200)

                imageView.imageTintList = AppCompatResources.getColorStateList(this@MenuQuizActivity,
                    R.color.white)

                txtTitle.setTextColor(
                    AppCompatResources.getColorStateList(this@MenuQuizActivity,
                        R.color.white))

                txtTitle.text = "Kuis Menulis"
                imageView.setOnClickListener {
                    onBackPressed()
                }
            }
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
                val dialogStart = ConfirmationDialog(
                    this@MenuQuizActivity,
                    icon = R.drawable.ic_start_64,
                    title = "Mulai Kuis",
                    message = "Apakah Anda yakin ingin memulai mengerjakan soal?",
                    onConfirmClickListener = {
                        val intent = Intent(this, QuizAttemptActivity::class.java)
                        intent.putExtra("quizSetId", quizSet.id)
                        intent.putExtra("student", student)
                        startActivity(intent)
                    }
                )
                dialogStart.show()
            },
            onDeleteClick = { quizSet ->
                val dialogDelete = ConfirmationDialogRedStyle(
                    this@MenuQuizActivity,
                    icon = R.drawable.ic_baseline_delete_24,
                    title = "Apakah Anda yakin akan menghapus kuis ini?",
                    message = "kuis akan dihapus permanen",
                    onConfirmClickListener = {
                        deleteQuizSet(quizSet.id)
                        onResume()
                    }
                )
                dialogDelete.show()
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