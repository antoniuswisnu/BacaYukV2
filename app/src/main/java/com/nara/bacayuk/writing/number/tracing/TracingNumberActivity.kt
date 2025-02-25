package com.nara.bacayuk.writing.number.tracing

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nara.bacayuk.writing.number.MenuNumberActivity
import com.nara.bacayuk.R
import com.nara.bacayuk.databinding.ActivityTracingNumberBinding

class TracingNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTracingNumberBinding
    private lateinit var successDialog: Dialog
    private val currentNumber: String by lazy {
        intent?.getStringExtra(EXTRA_NUMBER) ?: "0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTracingNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        successDialog = Dialog(this, R.style.CustomDialog)
        successDialog.setContentView(R.layout.success_dialog)
        successDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        successDialog.dismiss()

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
            startActivity(Intent(this, MenuNumberActivity::class.java))
        }

        binding.tracingCanvas.setOnCorrectTracingListener {
            successDialog.show()
        }

        successDialog.findViewById<Button>(R.id.btn_lanjutkan)?.setOnClickListener {
            successDialog.dismiss()
            startActivity(Intent(this, TracingNumberActivity::class.java))
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