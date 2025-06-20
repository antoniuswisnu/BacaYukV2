package com.nara.bacayuk.writing.letter.tracing.lowercase

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.ReportTulisHuruf
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.databinding.ActivityTracingLetterLowercaseBinding
import com.nara.bacayuk.ui.custom_view.AnswerStatusDialog
import com.nara.bacayuk.ui.custom_view.OnDismissDialog
import com.nara.bacayuk.writing.letter.animation.lowercase.LetterAnimationLowercaseActivity
import com.nara.bacayuk.writing.letter.menu.MenuLetterActivity
import com.nara.bacayuk.writing.letter.tracing.capital.TracingLetterCapitalActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracingLetterLowercaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingLetterLowercaseBinding
    private val currentLetter: String by lazy {
        intent?.getStringExtra(TracingLetterCapitalActivity.EXTRA_LETTER) ?: "a"
    }
    var student: Student? = null
    private val tracingLetterLowercaseViewModel: TracingLetterLowercaseViewModel by viewModel()
    private var tulis: Tulis? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracingLetterLowercaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        tulis = Tulis(reportTulisHuruf = ReportTulisHuruf())

        loadLetter()

        binding.btnPlayTutorial.setOnClickListener {
            startActivity(Intent(this, LetterAnimationLowercaseActivity::class.java).apply {
                putExtra(LetterAnimationLowercaseActivity.EXTRA_LETTER, currentLetter.lowercase())
                putExtra("student", student)
            })
        }

        binding.btnPencil.setOnClickListener {
            binding.tracingCanvas.setDrawingMode(true)
            binding.btnPencil.setImageResource(R.drawable.ic_pencil_active)
        }

        binding.btnReload.setOnClickListener {
            binding.tracingCanvas.clearCanvas()
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, MenuLetterActivity::class.java).apply{
                putExtra("student", student)
            })
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            sendData()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.tvTitle.text = currentLetter
    }

    private fun sendData(){
        val user: User? = tracingLetterLowercaseViewModel.getUserDataStore()
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
        tulis?.reportTulisHuruf?.latihanTulisHurufNonKapital = true
        tulis?.reportTulisHuruf?.tulisHuruf = currentLetter.uppercase()

        val reportTulisHuruf = tulis?.reportTulisHuruf
        if (reportTulisHuruf != null && user != null && student != null) {
            user.uuid?.let {
                tracingLetterLowercaseViewModel.updateReportHurufKecil(
                    it,
                    student?.uuid ?: "",
                    reportTulisHuruf
                )
            }
        }
    }

    private fun loadLetter() {
        binding.tracingCanvas.setLetter(currentLetter)
        binding.tracingCanvas.clearCanvas()
    }

    companion object {
        const val EXTRA_LETTER = "SELECTED_LETTER"
    }
}