package com.nara.bacayuk.writing.word.menu

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayout
import com.nara.bacayuk.data.model.ReportTulisKata
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.databinding.ActivityMenuWordBinding
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.ui.feat_menu_utama.MainActivity
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.writing.word.tracing.TracingWordActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.nara.bacayuk.R

class MenuWordActivity : AppCompatActivity(), AdapterListener {

    private lateinit var binding: ActivityMenuWordBinding
    private val menuWordViewModel: MenuWordViewModel by viewModel()
    private val dialog by lazy { waitingDialog() }

    private val easyWordAdapter by lazy { WordAdapter(this, this::showWordActionsDialog) }
    private val mediumWordAdapter by lazy { WordAdapter(this, this::showWordActionsDialog) }
    private val hardWordAdapter by lazy { WordAdapter(this, this::showWordActionsDialog) }

    var student: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("student") as Student?
        }

        if (student == null) {
            Toast.makeText(this, "Data siswa tidak ditemukan.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupUI()
        observeViewModel()

        student?.uuid?.let { menuWordViewModel.fetchAllWords(it) }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.rvKata.layoutManager = GridLayoutManager(this@MenuWordActivity, 2)
        binding.rvKata.adapter = easyWordAdapter

        binding.tabLayoutWords.addTab(binding.tabLayoutWords.newTab().setText("Mudah"))
        binding.tabLayoutWords.addTab(binding.tabLayoutWords.newTab().setText("Sedang"))
        binding.tabLayoutWords.addTab(binding.tabLayoutWords.newTab().setText("Sulit"))

        binding.tabLayoutWords.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.rvKata.adapter = easyWordAdapter
                        menuWordViewModel.easyWords.value?.let { easyWordAdapter.submitData(mapToTulisList(it)) }
                    }
                    1 -> {
                        binding.rvKata.adapter = mediumWordAdapter
                        menuWordViewModel.mediumWords.value?.let { mediumWordAdapter.submitData(mapToTulisList(it)) }
                    }
                    2 -> {
                        binding.rvKata.adapter = hardWordAdapter
                        menuWordViewModel.hardWords.value?.let { hardWordAdapter.submitData(mapToTulisList(it)) }
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.fabAddWord.setOnClickListener {
            showAddOrEditWordDialog(null)
        }
    }

    private fun observeViewModel() {
        menuWordViewModel.allWords.observe(this) { response ->
            when (response) {
                is Response.Loading -> dialog.show()
                is Response.Success -> {
                    dialog.dismiss()
                    updateAdaptersBasedOnActiveTab()
                }
                is Response.Error -> {
                    dialog.dismiss()
                    Toast.makeText(this, "Error: ${response.message}", Toast.LENGTH_LONG).show()
                    Log.e("MenuWordActivity", "Error fetching words: ${response.message}")
                }
            }
        }

        menuWordViewModel.easyWords.observe(this) { words ->
            if (binding.tabLayoutWords.selectedTabPosition == 0) {
                easyWordAdapter.submitData(mapToTulisList(words))
            }
        }
        menuWordViewModel.mediumWords.observe(this) { words ->
            if (binding.tabLayoutWords.selectedTabPosition == 1) {
                mediumWordAdapter.submitData(mapToTulisList(words))
            }
        }
        menuWordViewModel.hardWords.observe(this) { words ->
            if (binding.tabLayoutWords.selectedTabPosition == 2) {
                hardWordAdapter.submitData(mapToTulisList(words))
            }
        }

        menuWordViewModel.addWordStatus.observe(this) { response ->
            when (response) {
                is Response.Loading -> dialog.show()
                is Response.Success -> {
                    dialog.dismiss()
                    Toast.makeText(this, "Kata berhasil ditambahkan (ID: ${response.data})", Toast.LENGTH_SHORT).show()
                }
                is Response.Error -> {
                    dialog.dismiss()
                    Toast.makeText(this, "Gagal menambah kata: ${response.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        menuWordViewModel.updateWordStatus.observe(this) { response ->
            when (response) {
                is Response.Loading -> dialog.show()
                is Response.Success -> {
                    dialog.dismiss()
                    if(response.data) Toast.makeText(this, "Kata berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this, "Gagal memperbarui kata (status false)", Toast.LENGTH_SHORT).show()
                }
                is Response.Error -> {
                    dialog.dismiss()
                    Toast.makeText(this, "Gagal memperbarui kata: ${response.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        menuWordViewModel.deleteWordStatus.observe(this) { response ->
            when (response) {
                is Response.Loading -> dialog.show()
                is Response.Success -> {
                    dialog.dismiss()
                    if(response.data) Toast.makeText(this, "Kata berhasil dihapus", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this, "Gagal menghapus kata (status false)", Toast.LENGTH_SHORT).show()
                }
                is Response.Error -> {
                    dialog.dismiss()
                    Toast.makeText(this, "Gagal menghapus kata: ${response.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateAdaptersBasedOnActiveTab() {
        when (binding.tabLayoutWords.selectedTabPosition) {
            0 -> menuWordViewModel.easyWords.value?.let { easyWordAdapter.submitData(mapToTulisList(it)) }
            1 -> menuWordViewModel.mediumWords.value?.let { mediumWordAdapter.submitData(mapToTulisList(it)) }
            2 -> menuWordViewModel.hardWords.value?.let { hardWordAdapter.submitData(mapToTulisList(it)) }
        }
    }


    private fun mapToTulisList(reports: List<ReportTulisKata>): ArrayList<Tulis> {
        return ArrayList(reports.map { report ->
            Tulis(
                id = report.id, // Gunakan ID unik dari ReportTulisKata
                tulisKata = report.tulisKata,
                reportTulisKata = report // reportTulisKata sekarang memiliki field 'level' dan 'id'
            )
        })
    }

    override fun onClick(data: Any?, position: Int?, view: View?, type: String) {
        if (data is Tulis) {
            val intent = Intent(this@MenuWordActivity, TracingWordActivity::class.java)
                .apply {
                    putExtra("student", student)
                    putExtra(EXTRA_WORD, data.tulisKata)
                }
            startActivity(intent)
        } else {
            Log.e("MenuWordActivity", "Data is not Tulis or null")
        }
    }

    private fun showWordActionsDialog(reportTulisKata: ReportTulisKata) {
        val options = arrayOf("Edit Kata", "Hapus Kata")
        AlertDialog.Builder(this)
            .setTitle("Aksi untuk '${reportTulisKata.tulisKata}'")
            .setItems(options) { dialogInterface, which ->
                when (which) {
                    0 -> showAddOrEditWordDialog(reportTulisKata)
                    1 -> showDeleteConfirmationDialog(reportTulisKata)
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }


    // Dialog untuk menambah atau mengedit kata
    private fun showAddOrEditWordDialog(existingWord: ReportTulisKata?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_word, null)
        val editTextWord = dialogView.findViewById<EditText>(R.id.editTextWordContent)

        val title = if (existingWord == null) "Tambah Kata Baru" else "Edit Kata"
        existingWord?.let { editTextWord.setText(it.tulisKata) }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton(if (existingWord == null) "Tambah" else "Simpan") { dialog, _ ->
                val wordText = editTextWord.text.toString().trim()
                if (wordText.isBlank()) {
                    Toast.makeText(this, "Kata tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (wordText.length > 5) {
                    Toast.makeText(this, "Kata maksimal 5 huruf.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                student?.uuid?.let { studentId ->
                    if (existingWord == null) {
                        menuWordViewModel.addNewWord(studentId, wordText)
                    } else {
                        val updatedWord = existingWord.copy(tulisKata = wordText)
                        menuWordViewModel.updateExistingWord(studentId, updatedWord)
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // Dialog konfirmasi untuk menghapus kata
    private fun showDeleteConfirmationDialog(wordToDelete: ReportTulisKata) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kata")
            .setMessage("Apakah Anda yakin ingin menghapus kata '${wordToDelete.tulisKata}'?")
            .setPositiveButton("Hapus") { dialog, _ ->
                student?.uuid?.let { studentId ->
                    menuWordViewModel.deleteSpecificWord(studentId, wordToDelete.id)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }


    override fun onResume() {
        super.onResume()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@MenuWordActivity, MainActivity::class.java).apply {
            putExtra("student", student)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_WORD = "SELECTED_WORD"
    }
}
