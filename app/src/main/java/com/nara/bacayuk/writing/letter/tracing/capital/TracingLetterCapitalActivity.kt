package com.nara.bacayuk.writing.letter.tracing.capital

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.writing.letter.tracing.lowercase.TracingLetterLowercaseActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.ReportTulisHuruf
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.databinding.ActivityTracingLetterCapitalBinding
import com.nara.bacayuk.ui.custom_view.AnswerStatusDialog
import com.nara.bacayuk.ui.custom_view.OnDismissDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracingLetterCapitalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingLetterCapitalBinding
    private val currentLetter: String by lazy {
        intent?.getStringExtra(EXTRA_LETTER) ?: "A"
    }
    var student: Student? = null
    private val tracingLetterCapitalViewModel : TracingLetterCapitalViewModel by viewModel()
    private var tulis: Tulis? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracingLetterCapitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        tulis = Tulis(reportTulisHuruf = ReportTulisHuruf())

        loadLetter()

        binding.btnPlayTutorial.setOnClickListener {
            finish()
        }

        binding.btnPencil.setOnClickListener {
            binding.tracingCanvas.setDrawingMode(true)
            binding.btnPencil.setImageResource(R.drawable.ic_pencil_active)
        }

        binding.btnReload.setOnClickListener {
            binding.tracingCanvas.clearCanvas()
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, TracingLetterLowercaseActivity::class.java).apply {
                putExtra(TracingLetterLowercaseActivity.EXTRA_LETTER, currentLetter.lowercase())
                putExtra("student", student)
            })
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            sendData()
        }

        binding.tvTitle.text = currentLetter.uppercase()
    }

    private fun sendData(){
        val user: User? = tracingLetterCapitalViewModel.getUserDataStore()
        val dialog = AnswerStatusDialog(
            context = this,
            icon = R.drawable.ic_checklist,
            status = "Benar",
            object: OnDismissDialog {
                override fun onDismissDialog() {
                }
            }
        )
        dialog.show()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.horizontalMargin = 0.1f
        dialog.window?.setAttributes(layoutParams)

        tulis?.reportTulisHuruf?.materiTulisHurufKapital = true
        tulis?.reportTulisHuruf?.materiTulisHurufNonKapital = true
        tulis?.reportTulisHuruf?.latihanTulisHurufKapital = true
        tulis?.reportTulisHuruf?.tulisHuruf = currentLetter

        val reportTulisHuruf = tulis?.reportTulisHuruf
        if (reportTulisHuruf != null && user != null && student != null) {
            user.uuid?.let {
                tracingLetterCapitalViewModel.updateReportHurufKapital(
                    it,
                    student?.uuid ?: "",
                    reportTulisHuruf
                )
            }
        }
    }

    private fun loadLetter() {
        binding.tracingCanvas.setLetter(currentLetter.uppercase())
        binding.tracingCanvas.clearCanvas()
    }

    companion object {
        const val EXTRA_LETTER = "SELECTED_LETTER"
    }
}
