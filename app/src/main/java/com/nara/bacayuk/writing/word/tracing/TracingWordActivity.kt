package com.nara.bacayuk.writing.word.tracing

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.ReportTulisKata
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.databinding.ActivityTracingWordBinding
import com.nara.bacayuk.ui.custom_view.AnswerStatusDialog
import com.nara.bacayuk.ui.custom_view.OnDismissDialog
import com.nara.bacayuk.writing.word.menu.MenuWordActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracingWordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingWordBinding
    private val currentWord: String by lazy {
        intent?.getStringExtra(EXTRA_WORD) ?: "word"
    }
    var student: Student? = null
    private val tracingWordViewModel: TracingWordViewModel by viewModel()
    private var tulis: Tulis? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracingWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        student = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("student", Student::class.java)
        } else {
            intent.getParcelableExtra("student") as Student?
        }

        tulis = Tulis(reportTulisKata = ReportTulisKata())

        loadWord()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        binding.btnPencil.setOnClickListener {
            binding.tracingCanvas.setDrawingMode(true)
            binding.btnPencil.setImageResource(R.drawable.ic_pencil_active)
        }

        binding.btnReload.setOnClickListener {
            binding.tracingCanvas.clearCanvas()
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MenuWordActivity::class.java)
                .apply {
                    putExtra("student", student)
                }
            )
            finish()
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, MenuWordActivity::class.java)
                .apply {
                    putExtra("student", student)
                })
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            sendData()
        }
    }

    private fun sendData(){
        val user: User? = tracingWordViewModel.getUserDataStore()
        Log.d("tracingWordActivity", "user: $user")
        Log.d("tracingWordActivity", "student: $student")
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

        tulis?.reportTulisKata?.materiTulisKata = true
        tulis?.reportTulisKata?.latihanTulisKata = true
        tulis?.reportTulisKata?.tulisKata = currentWord

        val reportTulisKata = tulis?.reportTulisKata
        if (reportTulisKata != null && user != null && student != null) {
            user.uuid?.let {
                tracingWordViewModel.updateReportKata(
                    it,
                    student?.uuid ?: "-",
                    reportTulisKata
                )
            }
        }

        binding.tvTitle.text = currentWord
    }

    private fun loadWord() {
        binding.tracingCanvas.setWord(currentWord)
        binding.tracingCanvas.clearCanvas()
    }

    companion object {
        const val EXTRA_WORD = "SELECTED_WORD"
    }
}