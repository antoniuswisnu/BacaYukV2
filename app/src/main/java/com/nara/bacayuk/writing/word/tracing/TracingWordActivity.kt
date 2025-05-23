package com.nara.bacayuk.writing.word.tracing

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.ReportTulisKata
import com.nara.bacayuk.data.model.Response
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.databinding.ActivityTracingWordBinding
import com.nara.bacayuk.ui.custom_view.AnswerStatusDialog
import com.nara.bacayuk.ui.custom_view.OnDismissDialog
import com.nara.bacayuk.ui.custom_view.waitingDialog
import com.nara.bacayuk.writing.word.menu.MenuWordActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracingWordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingWordBinding
    private val currentWordText: String by lazy { // Mengganti nama agar lebih jelas ini adalah teks kata
        intent?.getStringExtra(EXTRA_WORD) ?: ""
    }
    var student: Student? = null
    private val tracingWordViewModel: TracingWordViewModel by viewModel()
    private var currentActiveReport: ReportTulisKata? = null // Untuk menyimpan ReportTulisKata yang aktif
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
            Log.e("TracingWordActivity", "Student or currentWordText is null/blank. Student: $student, Word: $currentWordText")
            finish()
            return
        }

        setupViewModelObservers()
        fetchWordDetails() // Panggil fungsi untuk mengambil detail kata

        binding.tvTitle.text = currentWordText // Set judul dengan teks kata awal
        loadWordToCanvas() // Muat kata ke canvas

        binding.btnPencil.setOnClickListener {
            binding.tracingCanvas.setDrawingMode(true)
            binding.btnPencil.setImageResource(R.drawable.ic_pencil_active)
            // Tambahkan logika untuk menonaktifkan eraser jika ada
        }

        binding.btnReload.setOnClickListener {
            binding.tracingCanvas.clearCanvas()
        }

        binding.btnBack.setOnClickListener {
            navigateToMenuWordActivity()
        }

        binding.btnNext.setOnClickListener {
            // Tombol Selesai/Next mungkin lebih cocok untuk memicu sendData jika tracing sudah benar
            // atau navigasi jika sudah selesai dan disimpan.
            // Untuk saat ini, asumsikan sendData dipanggil oleh onCorrectTracing
            // Jika sendData berhasil, baru navigasi.
            // Jika belum ada progres, mungkin navigasi saja.
            if (currentActiveReport?.materiTulisKata == true && currentActiveReport?.latihanTulisKata == true) {
                navigateToMenuWordActivity()
            } else {
                // Mungkin tampilkan pesan bahwa latihan belum selesai atau biarkan user menyelesaikan
                Toast.makeText(this, "Selesaikan latihan menulis terlebih dahulu.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            // Hanya panggil sendData jika currentActiveReport sudah ada (berhasil di-fetch)
            if (currentActiveReport != null) {
                sendData()
            } else {
                Toast.makeText(this, "Data kata belum siap, coba lagi.", Toast.LENGTH_SHORT).show()
                Log.w("TracingWordActivity", "onCorrectTracing called but currentActiveReport is null.")
                // Mungkin coba fetch lagi jika gagal sebelumnya
                fetchWordDetails()
            }
        }
    }

    private fun fetchWordDetails() {
        val user = tracingWordViewModel.getUserDataStore()
        if (user?.uuid != null && student?.uuid != null && currentWordText.isNotBlank()) {
            tracingWordViewModel.fetchSpecificWordReport(user.uuid!!, student!!.uuid!!, currentWordText)
        } else {
            Toast.makeText(this, "Gagal memuat detail kata: Informasi pengguna/siswa tidak lengkap.", Toast.LENGTH_LONG).show()
            Log.e("TracingWordActivity", "Cannot fetch word details. User UUID: ${user?.uuid}, Student UUID: ${student?.uuid}, Word: $currentWordText")
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
                    if (currentActiveReport == null) {
                        Toast.makeText(this, "Detail kata tidak ditemukan.", Toast.LENGTH_LONG).show()
                        Log.e("TracingWordActivity", "Fetched specific word report but data is null for word: $currentWordText")
                        // finish() // Pertimbangkan untuk menutup activity jika kata tidak ada
                    } else {
                        Log.i("TracingWordActivity", "Successfully fetched word: ${currentActiveReport?.tulisKata}, ID: ${currentActiveReport?.id}")
                        // Update UI jika perlu berdasarkan data yang sudah ada (misal, status selesai)
                    }
                }
                is Response.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error memuat detail kata: ${response.message}", Toast.LENGTH_LONG).show()
                    Log.e("TracingWordActivity", "Error fetching specific word report: ${response.message}")
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
                        showSuccessDialog() // Panggil dialog sukses setelah update berhasil
                        Log.i("TracingWordActivity", "Progres kata berhasil disimpan ke Firestore.")
                        // Refresh local currentActiveReport state after successful update
                        currentActiveReport = currentActiveReport?.copy(materiTulisKata = true, latihanTulisKata = true)

                    } else {
                        Toast.makeText(this, "Gagal menyimpan progres kata (status false).", Toast.LENGTH_LONG).show()
                        Log.e("TracingWordActivity", "Failed to save word progress to Firestore (status false).")
                    }
                }
                is Response.Error -> {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error menyimpan progres: ${response.message}", Toast.LENGTH_LONG).show()
                    Log.e("TracingWordActivity", "Error saving word progress: ${response.message}")
                }
            }
        }
    }


    private fun sendData() {
        val user = tracingWordViewModel.getUserDataStore()
        if (user?.uuid == null || student?.uuid == null) {
            Toast.makeText(this, "Informasi pengguna atau siswa tidak lengkap.", Toast.LENGTH_SHORT).show()
            Log.e("TracingWordActivity", "User or student UUID is null in sendData.")
            return
        }

        if (currentActiveReport == null) {
            Toast.makeText(this, "Tidak ada data kata aktif untuk disimpan.", Toast.LENGTH_SHORT).show()
            Log.e("TracingWordActivity", "currentActiveReport is null in sendData, cannot update.")
            // Coba fetch lagi jika ini terjadi secara tidak terduga
            // fetchWordDetails()
            return
        }

        // Buat salinan dari currentActiveReport dan update field yang diperlukan
        // Pastikan ID dan field lain dari currentActiveReport (yang dari Firestore) tetap terjaga
        val reportToUpdate = currentActiveReport!!.copy(
            materiTulisKata = true,
            latihanTulisKata = true
            // `tulisKata` dan `level` sudah ada di `currentActiveReport`, `id` juga.
            // `audioUrl` juga akan terbawa dari `currentActiveReport`.
        )

        Log.d("TracingWordActivity", "Sending data for update: User: ${user.uuid}, Student: ${student!!.uuid}, Report: $reportToUpdate")
        tracingWordViewModel.updateWordProgress(user.uuid!!, student!!.uuid!!, reportToUpdate)
    }

    private fun showSuccessDialog() {
        val dialog = AnswerStatusDialog(
            context = this,
            icon = R.drawable.ic_checklist, // Pastikan drawable ini ada
            status = "Benar", // Atau "Selesai"
            object : OnDismissDialog {
                override fun onDismissDialog() {
                    // Bisa tambahkan aksi setelah dialog ditutup, misal navigasi
                    // navigateToMenuWordActivity() // Pindah navigasi ke setelah observer updateProgressStatus
                }
            }
        )
        // Konfigurasi WindowManager.LayoutParams jika masih diperlukan
        try {
            dialog.show()
            val layoutParams = WindowManager.LayoutParams()
            dialog.window?.let {
                layoutParams.copyFrom(it.attributes)
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT // Atau ukuran spesifik
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                // layoutParams.horizontalMargin = 0.1f // Hati-hati dengan margin, sesuaikan
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
            // flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP // Agar tidak menumpuk
        }
        startActivity(intent)
        finish() // Tutup TracingWordActivity
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToMenuWordActivity()
    }

    companion object {
        const val EXTRA_WORD = "SELECTED_WORD"
    }
}
