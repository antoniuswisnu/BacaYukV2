package com.nara.bacayuk.writing.letter.tracing.lowercase

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.ActivityTracingLetterLowercaseBinding
import com.nara.bacayuk.writing.letter.menu.MenuLetterActivity
import com.nara.bacayuk.writing.letter.tracing.capital.TracingLetterCapitalActivity

class TracingLetterLowercaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingLetterLowercaseBinding
    private lateinit var successDialog: Dialog
    private val currentLetter: String by lazy {
        intent?.getStringExtra(TracingLetterCapitalActivity.EXTRA_LETTER) ?: "a"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracingLetterLowercaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        successDialog = Dialog(this, R.style.CustomDialog)
        successDialog.setContentView(R.layout.success_dialog)
        successDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        successDialog.dismiss()

        loadLetter()

        binding.btnPlayTutorial.setOnClickListener {
            finish()
        }

        binding.btnPencil.setOnClickListener {
            binding.tracingCanvas.setDrawingMode(true)
        }

        binding.btnEraser.setOnClickListener {
            binding.tracingCanvas.setDrawingEraser(true)
        }

        binding.btnReload.setOnClickListener {
            binding.tracingCanvas.clearCanvas()
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, MenuLetterActivity::class.java))
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            successDialog.show()
        }

        binding.tvTitle.text = currentLetter
    }

    private fun loadLetter() {
        binding.tracingCanvas.setLetter(currentLetter)
        binding.tracingCanvas.clearCanvas()
    }

    companion object {
        const val EXTRA_LETTER = "SELECTED_LETTER"
    }
}