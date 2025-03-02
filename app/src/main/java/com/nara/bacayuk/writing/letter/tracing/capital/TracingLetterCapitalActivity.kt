package com.nara.bacayuk.writing.letter.tracing.capital

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.writing.letter.tracing.lowercase.TracingLetterLowercaseActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.ActivityTracingLetterCapitalBinding

class TracingLetterCapitalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingLetterCapitalBinding
    private lateinit var successDialog: Dialog
    private val currentLetter: String by lazy {
        intent?.getStringExtra(EXTRA_LETTER) ?: "A"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracingLetterCapitalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadLetter()

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
            startActivity(Intent(this, TracingLetterLowercaseActivity::class.java).apply {
                putExtra(TracingLetterLowercaseActivity.EXTRA_LETTER, currentLetter.lowercase())
            })
        }

        successDialog.findViewById<Button>(R.id.btn_lanjutkan)?.setOnClickListener {
            successDialog.dismiss()
            startActivity(Intent(this, TracingLetterLowercaseActivity::class.java))
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            showSuccessDialog()
        }

        binding.tvTitle.text = currentLetter.uppercase()
    }

    private fun loadLetter() {
        binding.tracingCanvas.setLetter(currentLetter.uppercase())
        binding.tracingCanvas.clearCanvas()
    }

    private fun showSuccessDialog() {
        successDialog = Dialog(this)
        successDialog.setContentView(R.layout.success_dialog)
        successDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        successDialog.setCancelable(false)
    }

    companion object {
        const val EXTRA_LETTER = "SELECTED_LETTER"
    }
}
