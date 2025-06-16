package com.nara.bacayuk.writing.quiz.menu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.nara.bacayuk.writing.quiz.tracing.QuizAttemptActivity
import com.nara.bacayuk.writing.quiz.question.ListQuestionActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityMenuQuizBinding
import com.nara.bacayuk.ui.custom_view.AddQuizSetDialog
import com.nara.bacayuk.ui.custom_view.ConfirmationDialog
import com.nara.bacayuk.ui.custom_view.ConfirmationDialogRedStyle
import com.nara.bacayuk.utils.invisible

class MenuQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuQuizBinding
    private lateinit var adapter: MenuQuizAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var student: Student? = null

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

        if (student == null || auth.currentUser == null) {
            Toast.makeText(this, "Data siswa atau pengguna tidak ditemukan.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupRecyclerView()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
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
                    message = "Semua soal di dalamnya juga akan terhapus permanen.",
                    onConfirmClickListener = {
                        deleteQuizSet(quizSet.id)
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
            },
            onUpdateClick = { quizSet ->
                val addQuizSetDialog = AddQuizSetDialog(
                    context = this,
                    title = "Edit Kuis",
                    message = "Ubah judul dan deskripsi kuis ini",
                    titleQuiz = quizSet.title,
                    descQuiz = quizSet.description,
                    onConfirmClickListener = { title, description ->
                        editQuizSet(quizSet.id, title, description)
                    }
                )
                addQuizSetDialog.show()
                true
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

    private fun getStudentBaseCollection() =
        firestore.collection("Users").document(auth.currentUser!!.uid)
            .collection("Students").document(student!!.uuid)

    private fun showCreateQuizSetDialog() {
        val addQuizSetDialog = AddQuizSetDialog(
            context = this,
            title = "Buat Kuis Baru",
            message = "Masukkan judul dan deskripsi kuis baru",
            titleQuiz = "",
            descQuiz = "",
            onConfirmClickListener = { titleQuiz, descQuiz ->
                if (titleQuiz.isNotBlank()) {
                    createQuizSet(titleQuiz, descQuiz)
                } else {
                    Toast.makeText(this, "Judul kuis tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
        )
        addQuizSetDialog.show()
    }

    private fun createQuizSet(title: String, description: String) {
        val quizSetsCollection = getStudentBaseCollection().collection("quizSets")

        val newDoc = quizSetsCollection.document()
        val quizSet = MenuQuiz(
            id = newDoc.id,
            title = title,
            description = description,
        )

        newDoc.set(quizSet)
            .addOnSuccessListener {
                Log.d("MenuQuizActivity", "Quiz set created successfully.")
                loadQuizSets()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("MenuQuizActivity", "Error creating quiz set: ${e.message}")
            }
    }

    private fun editQuizSet(quizSetId: String, newTitle: String, newDescription: String) {
        val quizSetDoc = getStudentBaseCollection().collection("quizSets").document(quizSetId)

        quizSetDoc.update("title", newTitle, "description", newDescription)
            .addOnSuccessListener {
                Toast.makeText(this, "Kuis berhasil diperbarui", Toast.LENGTH_SHORT).show()
                Log.d("MenuQuizActivity", "Quiz set updated successfully.")
                loadQuizSets()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memperbarui kuis: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("MenuQuizActivity", "Error updating quiz set: ${e.message}")
            }
    }

    private fun loadQuizSets() {
        getStudentBaseCollection().collection("quizSets")
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
        val studentCollection = getStudentBaseCollection()
        val quizSetDoc = studentCollection.collection("quizSets").document(quizSetId)
        val questionsQuery = studentCollection.collection("quizzes").whereEqualTo("quizSetId", quizSetId)

        questionsQuery.get().addOnSuccessListener { questionsSnapshot ->
            firestore.runBatch { batch ->
                batch.delete(quizSetDoc)

                for (doc in questionsSnapshot.documents) {
                    batch.delete(doc.reference)
                }
            }.addOnSuccessListener {
                Toast.makeText(this, "Kuis berhasil dihapus", Toast.LENGTH_SHORT).show()
                Log.d("MenuQuizActivity", "Batch delete successful.")
                loadQuizSets()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menghapus kuis: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("MenuQuizActivity", "Error in batch delete: ${e.message}")
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Gagal menemukan soal terkait: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("MenuQuizActivity", "Error finding related questions: ${e.message}")
        }
    }
}