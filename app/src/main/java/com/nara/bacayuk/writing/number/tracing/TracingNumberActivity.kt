package com.nara.bacayuk.writing.number.tracing

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.writing.number.menu.MenuNumberActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.ReportTulisAngka
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.databinding.ActivityTracingNumberBinding
import com.nara.bacayuk.ui.customview.AnswerStatusDialog
import com.nara.bacayuk.ui.customview.OnDismissDialog
import com.nara.bacayuk.ui.feat_baca_huruf.quiz_baca_huruf.QuizBacaHurufActivity.Companion.student
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracingNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingNumberBinding
    private lateinit var successDialog: Dialog
    private val currentNumber: String by lazy {
        intent?.getStringExtra(EXTRA_NUMBER) ?: "0"
    }
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

//        successDialog = Dialog(this, R.style.CustomDialog)
//        successDialog.setContentView(R.layout.success_dialog)
//        successDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        successDialog.dismiss()

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
//            successDialog.show()
            val user: User? = tracingNumberViewModel.getUserDataStore()
            val dialog = AnswerStatusDialog(
                context = this,
                icon = R.drawable.ic_checklist,
                status =  "Benar",
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
            dialog.window?.setAttributes(layoutParams)
            tulis?.reportTulisAngka?.latihanAngka = true
            tulis?.reportTulisAngka?.materiAngka = true
            tulis?.reportTulisAngka?.tulisAngka = currentNumber
            val reportTulisAngka = tulis?.reportTulisAngka
            if (reportTulisAngka != null) {
                tracingNumberViewModel.updateReportAngka(
                    user?.uuid ?: "-",
                    student?.uuid ?: "-",
                    reportTulisAngka
                )
            } else {
                Log.e("TracingNumberActivity", "Report Tulis Angka is null")
            }
            Log.d("TracingNumberActivity", "User UUID: ${user?.uuid}")
            Log.d("TracingNumberActivity", "Student UUID: ${student?.uuid}")
            Log.d("TracingNumberActivity", "Report Tulis Angka: $reportTulisAngka")
        }

//        successDialog.findViewById<Button>(R.id.btn_lanjutkan)?.setOnClickListener {
//            successDialog.dismiss()
//            startActivity(Intent(this, TracingNumberActivity::class.java))
//        }

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