package com.nara.bacayuk.writing.number.tracing

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.writing.number.menu.MenuNumberActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.ReportTulisAngka
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.databinding.ActivityTracingNumberBinding
import com.nara.bacayuk.ui.custom_view.AnswerStatusDialog
import com.nara.bacayuk.ui.custom_view.OnDismissDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracingNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingNumberBinding
    private val currentNumber: String by lazy {
        intent?.getStringExtra(EXTRA_NUMBER) ?: "0"
    }
    var student: Student? = null
    private val tracingNumberViewModel: TracingNumberViewModel by viewModel()
    private var tulis: Tulis? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracingNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        tulis = Tulis(reportTulisAngka = ReportTulisAngka())

//        val user = tracingNumberViewModel.getUserDataStore()
//        if (user != null && student != null) {
//            user.uuid?.let { tracingNumberViewModel.getReportTulisAngka(it, student?.uuid ?: "") }
//        }

//        tracingNumberViewModel.reportTulisAngka.observe(this) { response ->
//            if (response is Response.Success && response.data.isNotEmpty()) {
//                val existingReport = response.data.firstOrNull()
//                if (existingReport != null) {
//                    tulis = Tulis(reportTulisAngka = existingReport)
//                    Log.d("TracingNumberActivity", "Loaded existing report: $existingReport")
//                }
//            }
//        }

        loadNumber()

        binding.btnPlayTutorial.setOnClickListener {
            finish()
        }

        binding.btnPencil.setOnClickListener {
            binding.tracingCanvas.setDrawingMode(true)
            binding.btnPencil.setImageResource(R.drawable.ic_pencil_active)
            binding.btnEraser.setImageResource(R.drawable.ic_eraser)
        }

        binding.btnEraser.setOnClickListener {
            binding.tracingCanvas.setDrawingEraser(true)
            binding.btnPencil.setImageResource(R.drawable.ic_pencil)
            binding.btnEraser.setImageResource(R.drawable.ic_eraser_active)
        }

        binding.btnReload.setOnClickListener {
            binding.tracingCanvas.clearCanvas()
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, MenuNumberActivity::class.java).apply {
                putExtra("student", student)
            })
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            val user: User? = tracingNumberViewModel.getUserDataStore()
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

            tulis?.reportTulisAngka?.latihanAngka = true
            tulis?.reportTulisAngka?.materiAngka = true
            tulis?.reportTulisAngka?.tulisAngka = currentNumber

            val reportTulisAngka = tulis?.reportTulisAngka
            if (reportTulisAngka != null && user != null && student != null) {
                user.uuid?.let {
                    tracingNumberViewModel.updateReportAngka(
                        it,
                        student?.uuid ?: "-",
                        reportTulisAngka
                    )
                }
                Log.d("TracingNumberActivity", "Updating report with: $reportTulisAngka")
            } else {
                Log.e("TracingNumberActivity", "Unable to update report. User: $user, Student: $student, Report: $reportTulisAngka")
            }
        }

        binding.tvTitle.text = currentNumber
    }

    private fun loadNumber() {
        binding.tracingCanvas.setNumber(currentNumber)
        binding.tracingCanvas.clearCanvas()
    }

    companion object {
        const val EXTRA_NUMBER = "SELECTED_NUMBER"
    }
}