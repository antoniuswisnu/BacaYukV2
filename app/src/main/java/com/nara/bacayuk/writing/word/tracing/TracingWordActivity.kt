package com.nara.bacayuk.writing.word.tracing

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.ReportTulisKata
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ActivityTracingWordBinding
import com.nara.bacayuk.ui.custom_view.AnswerStatusDialog
import com.nara.bacayuk.ui.custom_view.OnDismissDialog
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.writing.word.menu.MenuWordActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracingWordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingWordBinding
    private val currentWordText: String by lazy {
        intent?.getStringExtra(EXTRA_WORD) ?: ""
    }
    var student: Student? = null
    private val tracingWordViewModel: TracingWordViewModel by viewModel()
    private var currentActiveReport: ReportTulisKata? = null
    private val progressDialog by lazy { waitingDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracingWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("student") as Student?
        }

        if (student == null || currentWordText.isBlank()) {
            Toast.makeText(this, "Data siswa atau kata tidak valid.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupViewModelObservers()
        fetchWordDetails()

        binding.tvTitle.text = currentWordText
        loadWordToCanvas()

        binding.btnPencil.setOnClickListener {
            binding.tracingCanvas.setDrawingMode(true)
            binding.btnPencil.setImageResource(R.drawable.ic_pencil_active)
        }

        binding.btnReload.setOnClickListener {
            binding.tracingCanvas.resetCurrentLetterProgress()
        }

        binding.btnBack.setOnClickListener {
            navigateToMenuWordActivity()
        }

        binding.btnNext.setOnClickListener {
            if (currentActiveReport?.materiTulisKata == true && currentActiveReport?.latihanTulisKata == true) {
                navigateToMenuWordActivity()
            } else {
                Toast.makeText(this, "Selesaikan latihan menulis terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            if (currentActiveReport != null) {
                sendData()
            } else {
                Toast.makeText(this, "Data kata belum siap, coba lagi.", Toast.LENGTH_SHORT).show()
                fetchWordDetails()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkButtonVisibility()
        binding.tracingCanvas.setDrawingMode(false)
        binding.btnPencil.setImageResource(R.drawable.ic_pencil)
    }

    private fun checkButtonVisibility() {
        if (currentActiveReport?.materiTulisKata == true && currentActiveReport?.latihanTulisKata == true) {
            binding.btnNext.visibility = View.VISIBLE
        } else {
            binding.btnNext.visibility = View.GONE
        }
    }

    private fun fetchWordDetails() {
        val user = tracingWordViewModel.getUserDataStore()
        if (user?.uuid != null && student?.uuid != null && currentWordText.isNotBlank()) {
            tracingWordViewModel.fetchSpecificWordReport(user.uuid, student!!.uuid, currentWordText)
        } else {
            Toast.makeText(this, "Gagal memuat detail kata: Informasi pengguna/siswa tidak lengkap.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupViewModelObservers() {
        tracingWordViewModel.activeWordReport.observe(this) { response ->
            when (response) {
                is Response.Loading -> {
                    progressDialog.show()
                }
                is Response.Success -> {
                    progressDialog.dismiss()
                    currentActiveReport = response.data
                    checkButtonVisibility()
                    if (currentActiveReport == null) {
                        Toast.makeText(this, "Detail kata tidak ditemukan.", Toast.LENGTH_LONG).show()
                    } else {
                    }
                }
                is Response.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error memuat detail kata: ${response.message}", Toast.LENGTH_LONG).show()
                    // finish()
                }
            }
        }

        tracingWordViewModel.updateProgressStatus.observe(this) { response ->
            when (response) {
                is Response.Loading -> {
                    progressDialog.show()
                }
                is Response.Success -> {
                    progressDialog.dismiss()
                    if (response.data) {
                        showSuccessDialog()
                        currentActiveReport = currentActiveReport?.copy(materiTulisKata = true, latihanTulisKata = true)
                        checkButtonVisibility()
                    } else {
                        Toast.makeText(this, "Gagal menyimpan progres kata (status false).", Toast.LENGTH_LONG).show()
                    }
                }
                is Response.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error menyimpan progres: ${response.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun sendData() {
        val user = tracingWordViewModel.getUserDataStore()
        if (user?.uuid == null || student?.uuid == null) {
            Toast.makeText(this, "Informasi pengguna atau siswa tidak lengkap.", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentActiveReport == null) {
            Toast.makeText(this, "Tidak ada data kata aktif untuk disimpan.", Toast.LENGTH_SHORT).show()
            return
        }

        val reportToUpdate = currentActiveReport!!.copy(
            materiTulisKata = true,
            latihanTulisKata = true
        )

        tracingWordViewModel.updateWordProgress(user.uuid!!, student!!.uuid!!, reportToUpdate)
    }

    private fun showSuccessDialog() {
        val dialog = AnswerStatusDialog(
            context = this,
            icon = R.drawable.ic_checklist,
            status = "Benar",
            object : OnDismissDialog {
                override fun onDismissDialog() {
                }
            }
        )

        try {
            dialog.show()
            val layoutParams = WindowManager.LayoutParams()
            dialog.window?.let {
                layoutParams.copyFrom(it.attributes)
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                // layoutParams.horizontalMargin = 0.1f
                it.attributes = layoutParams
            }
        } catch (e: Exception) {
            Log.e("TracingWordActivity", "Error showing success dialog: ${e.message}", e)
        }
    }


    private fun loadWordToCanvas() {
        if (currentWordText.isNotBlank()) {
            binding.tracingCanvas.setWord(currentWordText)
            binding.tracingCanvas.clearCanvas()
        } else {
            Log.e("TracingWordActivity", "currentWordText is blank, cannot load to canvas.")
        }
    }

    private fun navigateToMenuWordActivity() {
        val intent = Intent(this, MenuWordActivity::class.java).apply {
            putExtra("student", student)
        }
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToMenuWordActivity()
    }

    companion object {
        const val EXTRA_WORD = "SELECTED_WORD"
    }
}
