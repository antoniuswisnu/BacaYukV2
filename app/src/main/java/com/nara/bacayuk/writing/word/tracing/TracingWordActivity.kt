package com.nara.bacayuk.writing.word.tracing

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.ActivityTracingWordBinding
import com.nara.bacayuk.writing.word.menu.MenuWordActivity

class TracingWordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingWordBinding
    private lateinit var successDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracingWordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        successDialog = Dialog(this, R.style.CustomDialog)
        successDialog.setContentView(R.layout.success_dialog)
        successDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        successDialog.dismiss()

        loadWord()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

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
            startActivity(Intent(this, MenuWordActivity::class.java))
        }

        successDialog.findViewById<Button>(R.id.btn_lanjutkan)?.setOnClickListener {
            successDialog.dismiss()
            startActivity(Intent(this, MenuWordActivity::class.java))
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            successDialog.show()
        }

        binding.tvTitle.text = intent.getStringExtra(EXTRA_WORD)
    }

    private fun loadWord() {
        val currentWord = intent.getStringExtra(EXTRA_WORD) ?: "Buku"
        binding.tracingCanvas.setWord(currentWord)
        binding.tracingCanvas.clearCanvas()
    }

    companion object {
        const val EXTRA_WORD = "SELECTED_WORD"
    }
}